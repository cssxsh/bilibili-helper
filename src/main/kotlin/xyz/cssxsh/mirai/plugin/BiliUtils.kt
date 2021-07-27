package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.mirai.plugin.tools.*
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal val logger by BiliHelperPlugin::logger

internal val client by lazy {
    object : BiliClient() {
        override val ignore: suspend (exception: Throwable) -> Boolean = { throwable ->
            super.ignore(throwable).also {
                if (it) logger.warning { "Ignore $throwable" }
            }
        }

        override val mutex: BiliApiMutex = BiliApiMutex(interval = BiliHelperSettings.api * 1000)
    }
}

internal val ImageCache by lazy { File(BiliHelperSettings.cache) }

internal val SetupSelenium by BiliHelperPlugin::selenium

internal val RemoteWebDriver by BiliHelperPlugin::driver

internal val DeviceName by lazy { SeleniumToolConfig.device }

enum class CacheType : Mutex by Mutex() {
    DYNAMIC,
    VIDEO,
    LIVE,
    SEASON,
    EPISODE,
    USER,
    EMOJI;
}

private val Url.filename get() = encodedPath.substringAfterLast("/")

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime =
        OffsetDateTime.parse(decoder.decodeString(), formatter)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) =
        encoder.encodeString(formatter.format(value))

}

/**
 * 通过正负号区分群和用户
 */
val Contact.delegate get() = if (this is Group) id * -1 else id

/**
 * 查找Contact
 */
fun findContact(delegate: Long): Contact? {
    Bot.instances.forEach { bot ->
        if (delegate < 0) {
            bot.getGroup(delegate * -1)?.let { return@findContact it }
        } else {
            bot.getFriend(delegate)?.let { return@findContact it }
            bot.getStranger(delegate)?.let { return@findContact it }
            bot.groups.forEach { group ->
                group.getMember(delegate)?.let { return@findContact it }
            }
        }
    }
    return null
}

private suspend fun Url.cache(type: CacheType, path: String, contact: Contact) = type.withLock {
    ImageCache.resolve(type.name).resolve(path).apply {
        if (exists().not()) {
            parentFile.mkdirs()
            writeBytes(client.useHttpClient { it.get(this@cache) })
        } else {
            setLastModified(System.currentTimeMillis())
        }
    }.uploadAsImage(contact)
}

private suspend fun Url.screenshot(type: CacheType, path: String, refresh: Boolean, contact: Contact) = type.withLock {
    ImageCache.resolve(type.name).resolve(path).apply {
        if (exists().not() || refresh) {
            parentFile.mkdirs()
            runCatching {
                RemoteWebDriver.getScreenshot(this@screenshot.toString())
            }.onFailure {
                logger.warning({ "使用SeleniumTool失败" }, it)
            }.getOrThrow().let {
                writeBytes(it)
            }
        } else {
            setLastModified(System.currentTimeMillis())
        }
    }.uploadAsImage(contact)
}

private suspend fun EmojiDetail.cache(contact: Contact): Image {
    return Url(url).cache(type = CacheType.EMOJI, path = "$name.${url.substringAfterLast('.')}", contact = contact)
}

internal suspend fun DynamicInfo.screenshot(contact: Contact, refresh: Boolean = false): Message {
    return runCatching {
        head.toPlainText() + Url(link).screenshot(
            type = CacheType.DYNAMIC,
            path = "${datetime.toLocalDate()}/${detail.id}.png",
            refresh = refresh,
            contact = contact
        )
    }.getOrElse {
        logger.warning({ "获取动态${detail.id}快照失败" }, it)
        content.toPlainText()
    }
}

internal suspend fun DynamicInfo.emoji(contact: Contact): Message {
    val details = display.emoji.details + display.origin?.emoji?.details.orEmpty()
    if (details.isEmpty()) return content.toPlainText()

    return buildMessageChain {
        var pos = 0
        while (pos < content.length) {
            val start = content.indexOf('[', pos).takeIf { it != -1 } ?: break
            val emoticon = details.find { content.startsWith(it.text, start) }

            if (emoticon == null) {
                add(content.substring(pos, start + 1))
                pos = start + 1
                continue
            }

            runCatching {
                emoticon.cache(contact)
            }.onSuccess {
                add(content.substring(pos, start))
                add(it)
            }.onFailure {
                logger.warning("获取BILI表情${emoticon.text}图片失败, $it")
                add(content.substring(pos, start + emoticon.text.length))
            }
            pos = start + emoticon.text.length
        }

        appendLine(content.substring(pos))
    }
}

internal suspend fun UserInfo.getFace(contact: Contact): Message {
    return Url(face).runCatching {
        cache(type = CacheType.USER, path = "${mid}/face-${filename}", contact = contact)
    }.getOrElse {
        logger.warning({ "获取[${mid}]头像失败" }, it)
        "获取[${mid}]头像失败".toPlainText()
    }
}

internal suspend fun DynamicInfo.getImages(contact: Contact) = images.mapIndexed { index, picture ->
    Url(picture).runCatching {
        cache(
            type = CacheType.DYNAMIC,
            path = "${datetime.toLocalDate()}/${detail.id}-${index}-${filename}",
            contact = contact
        )
    }.getOrElse {
        logger.warning({ "获取动态${detail.id}图片[${index}]失败" }, it)
        "获取动态${detail.id}图片[${index}]失败".toPlainText()
    }
}

internal suspend fun Live.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        cache(type = CacheType.LIVE, path = "${roomId}/cover-${filename}", contact = contact)
    }.getOrElse {
        logger.warning({ "获取[${roomId}]直播间封面失败" }, it)
        "获取[${roomId}]直播间封面失败".toPlainText()
    }
}

internal suspend fun Video.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        cache(type = CacheType.VIDEO, path = "${mid}/${id}-cover-${filename}", contact = contact)
    }.getOrElse {
        logger.warning({ "获取[${title}](${id})}视频封面失败" }, it)
        "获取[${title}](${id})}视频封面失败".toPlainText()
    }
}

internal suspend fun Season.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        cache(type = CacheType.SEASON, path = "${seasonId}/cover-${filename}", contact = contact)
    }.getOrElse {
        logger.warning({ "获取[${title}](${seasonId})}剧集封面失败" }, it)
        "获取[${title}](${seasonId})}剧集封面失败".toPlainText()
    }
}

internal suspend fun Episode.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        cache(type = CacheType.EPISODE, path = "${episodeId}/cover-${filename}", contact = contact)
    }.getOrElse {
        logger.warning({ "获取[${title}](${episodeId})}剧集封面失败" }, it)
        "获取[${title}](${episodeId})}剧集封面失败".toPlainText()
    }
}

internal suspend fun LiveRecord.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        cache(type = CacheType.LIVE, path = "${roomId}/cover-${filename}", contact = contact)
    }.getOrElse {
        logger.warning({ "获取[${roomId}]直播回放封面封面失败" }, it)
        "获取[${roomId}]直播回放封面失败".toPlainText()
    }
}