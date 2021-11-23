package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.sync.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import net.mamoe.mirai.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.openqa.selenium.remote.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.selenium.*
import java.io.File
import java.time.*
import java.time.format.*
import kotlin.properties.*
import kotlin.reflect.*

internal val logger by lazy {
    val open = System.getProperty("xyz.cssxsh.mirai.plugin.logger", "${true}").toBoolean()
    if (open) BiliHelperPlugin.logger else SilentLogger
}

@OptIn(ExperimentalSerializationApi::class)
internal var cookies by object : ReadWriteProperty<Any?, List<Cookie>> {
    private val json by lazy {
        BiliHelperPlugin.dataFolder.resolve("cookies.json").apply {
            if (exists().not()) createNewFile()
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<Cookie> {
        return BiliClient.Json.decodeFromString<List<EditThisCookie>>(json.readText().ifBlank { "[]" }).mapNotNull {
            try {
                it.toCookie()
            } catch (e: Throwable) {
                null
            }
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<Cookie>) {
        json.writeText(BiliClient.Json.encodeToString(value.mapIndexed { index, cookie ->
            cookie.toEditThisCookie(index)
        }))
    }
}

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

internal fun BiliClient.load() {
    storage.container.addAll(cookies)
}

internal fun BiliClient.save() {
    cookies = storage.container
}

/**
 * 注意避免意外情况的初始化
 */
internal lateinit var driver: RemoteWebDriver

internal suspend fun RemoteWebDriver.setHome(page: String, timeout: Long = 180_000): Map<String, Boolean>? {
    withTimeout(timeout) {
        get(page)
    }
    @Suppress("UNCHECKED_CAST")
    return executeScript("""return (window['selfBrowser'] || {})['version'] || {}""") as Map<String, Boolean>?
}

internal val ImageCache by lazy { File(BiliHelperSettings.cache) }

internal val ImageLimit by lazy { BiliHelperSettings.limit }

internal val SetupSelenium by BiliHelperPlugin::selenium

@Serializable
enum class CacheType : Mutex by Mutex() {
    DYNAMIC,
    VIDEO,
    LIVE,
    SEASON,
    EPISODE,
    USER,
    EMOJI;

    val directory get() = ImageCache.resolve(name)
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
    for (bot in Bot.instances) {
        if (delegate < 0) {
            for (group in bot.groups) {
                if (group.id == delegate * -1) return group
            }
        } else {
            for (friend in bot.friends) {
                if (friend.id == delegate) return friend
            }
            for (stranger in bot.strangers) {
                if (stranger.id == delegate) return stranger
            }
            for (friend in bot.friends) {
                if (friend.id == delegate) return friend
            }
            for (group in bot.groups) {
                for (member in group.members) {
                    if (member.id == delegate) return member
                }
            }
        }
    }
    return null
}

private suspend fun Url.cache(type: CacheType, path: String, contact: Contact) = type.withLock {
    type.directory.resolve(path).apply {
        if (exists().not()) {
            parentFile.mkdirs()
            writeBytes(client.useHttpClient { http, _ -> http.get(this@cache) })
        } else {
            setLastModified(System.currentTimeMillis())
        }
    }.uploadAsImage(contact)
}

private suspend fun Url.screenshot(type: CacheType, path: String, refresh: Boolean, contact: Contact) = type.withLock {
    type.directory.resolve(path).apply {
        if (exists().not() || refresh) {
            parentFile.mkdirs()
            writeBytes(driver.getScreenshot(url = this@screenshot.toString(), hide = BiliSeleniumConfig.hide))
        } else {
            setLastModified(System.currentTimeMillis())
        }
    }.uploadAsImage(contact)
}

private val FULLWIDTH_CHARS = mapOf(
    '\\' to '＼',
    '/' to '／',
    ':' to '：',
    '*' to '＊',
    '?' to '？',
    '"' to '＂',
    '<' to '＜',
    '>' to '＞',
    '|' to '｜'
)

internal fun String.toFullWidth(): String = fold("") { acc, char -> acc + (FULLWIDTH_CHARS[char] ?: char) }

private suspend fun EmojiDetail.cache(contact: Contact): Image {
    return Url(url).cache(
        type = CacheType.EMOJI,
        path = "${packageId}/${text.toFullWidth()}.${url.substringAfterLast('.')}",
        contact = contact
    )
}

internal suspend fun DynamicInfo.screenshot(contact: Contact, refresh: Boolean = false): Message {
    return try {
        head.toPlainText() + Url(h5).screenshot(
            type = CacheType.DYNAMIC,
            path = "${datetime.toLocalDate()}/${detail.id}.png",
            refresh = refresh,
            contact = contact
        )
    } catch (e: Throwable) {
        logger.warning({ "获取动态${detail.id}快照失败" }, e)
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

            emoticon.runCatching {
                cache(contact)
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
    if (ImageLimit > 0 && index >= ImageLimit) return@mapIndexed "图片[${index + 1}]省略".toPlainText()
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