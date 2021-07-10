package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.toPlainText
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

enum class CacheType {
    DYNAMIC,
    VIDEO,
    LIVE,
    SEASON,
    EPISODE,
    USER;
}

private val Url.filename get() = encodedPath.substringAfterLast("/")

object OffsetDateTimeSerializer: KSerializer<OffsetDateTime> {

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

private suspend fun getWebImage(url: Url, path: String, refresh: Boolean = false): File {
    return ImageCache.resolve(path).apply {
        if (exists().not() || refresh) {
            parentFile.mkdirs()
            writeBytes(client.useHttpClient { it.get(url) })
        }
    }
}

private suspend fun getScreenshot(url: String, path: String, refresh: Boolean = false): File {
    return ImageCache.resolve(path).apply {
        if (exists().not() || refresh) {
            parentFile.mkdirs()
            runCatching {
                RemoteWebDriver.getScreenshot(url)
            }.onFailure {
                logger.warning({ "使用SeleniumTool失败" }, it)
            }.getOrThrow().let {
                writeBytes(it)
            }
        }
    }
}

internal suspend fun DynamicInfo.getScreenshot(contact: Contact, refresh: Boolean = false): Message {
    return runCatching {
        head.toPlainText() + getScreenshot(
            url = link,
            path = "${CacheType.DYNAMIC}/${datetime.toLocalDate()}/${detail.dynamicId}.png",
            refresh = refresh
        ).uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取动态${detail.dynamicId}快照失败" }, it)
        content.toPlainText()
    }
}

internal suspend fun UserInfo.getFace(contact: Contact): Message {
    return Url(face).runCatching {
        getWebImage(url = this, path = "${CacheType.USER}/${mid}/face-${filename}").uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取[${mid}]头像失败" }, it)
        "获取[${mid}]头像失败".toPlainText()
    }
}

internal suspend fun DynamicInfo.getImageFiles(contact: Contact) = images.mapIndexed { index, picture ->
    Url(picture).runCatching {
        getWebImage(
            url = this,
            path = "${CacheType.DYNAMIC}/${datetime.toLocalDate()}/${detail.dynamicId}-${index}-${filename}"
        ).uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取动态${detail.dynamicId}图片[${index}]失败" }, it)
        "获取动态${detail.dynamicId}图片[${index}]失败".toPlainText()
    }
}

internal suspend fun Live.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.LIVE}/${roomId}/cover-${filename}").uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取[${roomId}]直播间封面失败" }, it)
        "获取[${roomId}]直播间封面失败".toPlainText()
    }
}

internal suspend fun Video.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.VIDEO}/${mid}/${id}-cover-${filename}").uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取[${title}](${id})}视频封面失败" }, it)
        "获取[${title}](${id})}视频封面失败".toPlainText()
    }
}

internal suspend fun Season.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.SEASON}/${seasonId}/cover-${filename}").uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取[${title}](${seasonId})}剧集封面失败" }, it)
        "获取[${title}](${seasonId})}剧集封面失败".toPlainText()
    }
}

internal suspend fun Episode.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.EPISODE}/${episodeId}/cover-${filename}").uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取[${title}](${episodeId})}剧集封面失败" }, it)
        "获取[${title}](${episodeId})}剧集封面失败".toPlainText()
    }
}

internal suspend fun LiveRecord.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.LIVE}/${roomId}/cover-${filename}").uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取[${roomId}]直播回放封面封面失败" }, it)
        "获取[${roomId}]直播回放封面失败".toPlainText()
    }
}