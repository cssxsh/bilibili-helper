package xyz.cssxsh.mirai.plugin

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.data.dynamic.*
import xyz.cssxsh.bilibili.data.live.*
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.client
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.data.SeleniumToolConfig.timeoutMillis
import xyz.cssxsh.mirai.plugin.data.SeleniumToolConfig.deviceName
import xyz.cssxsh.mirai.plugin.data.SeleniumToolConfig.driverType
import xyz.cssxsh.mirai.plugin.data.SeleniumToolConfig.driverUrl
import xyz.cssxsh.mirai.plugin.data.BilibiliHelperSettings.cacheDir
import xyz.cssxsh.mirai.plugin.tools.*
import java.io.File
import java.net.URL
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.*

internal val BILI_JSON = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

enum class CacheType {
    DYNAMIC,
    VIDEO,
    LIVE,
    RECORD;
}

/**
 * 2017-07-01 00:00:00
 */
private const val DYNAMIC_START = 1498838400L

@Suppress("unused")
internal fun dynamicTimestamp(id: Long): Long = (id shr 32) + DYNAMIC_START

private fun Url.getFilename() = encodedPath.substring(encodedPath.lastIndexOfAny(listOf("\\", "/")) + 1)

private fun getImage(type: CacheType, name: String): File =
    cacheDir.resolve(type.name).resolve(name)

private fun timestampToOffsetDateTime(timestamp: Long) =
    OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.systemDefault())

private suspend fun getBilibiliImage(
    url: Url,
    type: CacheType,
    name: String,
    refresh: Boolean = false
): File = getImage(type = type, name = name).apply {
    if (exists().not() || refresh) {
        parentFile.mkdirs()
        writeBytes(client.useHttpClient { it.get(url) })
    }
}

internal suspend fun getScreenShot(
    url: String,
    type: CacheType,
    name: String,
    refresh: Boolean = false
): File = getImage(type = type, name = name).apply {
    if (exists().not() || refresh) {
        parentFile.mkdirs()
        runCatching {
            SeleniumTool(
                remote = URL(driverUrl),
                deviceName = deviceName,
                type = driverType
            ).useDriver {
                it.getScreenShot(url, timeoutMillis)
            }
        }.onFailure {
            logger.warning({ "使用ChromeDriver(${driverUrl})失败" }, it)
        }.getOrElse {
            client.useHttpClient {
                it.get("https://www.screenshotmaster.com/api/screenshot") {
                    parameter("url", url)
                    parameter("width", 768)
                    parameter("height", 1024)
                    parameter("zone", "gz")
                    parameter("device", "table")
                    parameter("delay", 500)
                }
            }
        }.let {
            writeBytes(it)
        }
    }
}

internal suspend fun DynamicInfo.getScreenShot(refresh: Boolean = false) = getScreenShot(
    url = getDynamicUrl(),
    type = CacheType.DYNAMIC,
    name = "${getOffsetDateTime().toLocalDate()}/${describe.dynamicId}.png",
    refresh = refresh
)

internal fun DynamicInfo.isText() = when (describe.type) {
    DynamicType.REPLY, DynamicType.PICTURE, DynamicType.TEXT -> true
    else -> false
}

internal fun DynamicInfo.toMessageText() = buildString {
    when (describe.type) {
        DynamicType.REPLY -> {
            BILI_JSON.decodeFromJsonElement(DynamicReply.serializer(), card).let { card ->
                appendLine("RT ${card.originUser.user.uname}: ")
                appendLine(card.item.content)
            }
        }
        DynamicType.PICTURE -> {
            BILI_JSON.decodeFromJsonElement(DynamicPicture.serializer(), card).let { card ->
                appendLine(card.item.description)
            }
        }
        DynamicType.TEXT -> {
            BILI_JSON.decodeFromJsonElement(DynamicText.serializer(), card).let { card ->
                appendLine(card.item.content)
            }
        }
        DynamicType.MUSIC -> {
            BILI_JSON.decodeFromJsonElement(DynamicMusic.serializer(), card).let { card ->
                appendLine(card.intro)
                appendLine(card.title)
            }
        }
        DynamicType.VIDEO -> {
            BILI_JSON.decodeFromJsonElement(DynamicVideo.serializer(), card).let { card ->
                appendLine(card.title)
                appendLine(card.description)
                appendLine(card.jumpUrl)
            }
        }
        DynamicType.ARTICLE -> {
            BILI_JSON.decodeFromJsonElement(DynamicArticle.serializer(), card).let { card ->
                appendLine(card.title)
                appendLine(card.summary)
            }
        }
        DynamicType.LIVE -> {
            BILI_JSON.decodeFromJsonElement(DynamicLive.serializer(), card).let { card ->
                appendLine(card.title)
                appendLine(card.tags)
                appendLine(card.link)
            }
        }
        DynamicType.NONE -> {
            appendLine("不支持的类型${describe.type}")
        }
    }
}

internal fun DynamicInfo.getUserName() = describe.profile?.user?.uname ?: "【动态已删除】"

internal fun DynamicInfo.getOffsetDateTime() = timestampToOffsetDateTime(describe.timestamp)

internal fun DynamicInfo.getDynamicUrl() = "https://t.bilibili.com/${describe.dynamicId}"

internal suspend fun DynamicInfo.getImages() = buildList {
    if (describe.type == DynamicType.PICTURE) {
        BILI_JSON.decodeFromJsonElement(
            deserializer = DynamicPicture.serializer(),
            element = card
        ).item.pictures.forEachIndexed { index, picture ->
            runCatching {
                getBilibiliImage(
                    url = Url(picture.source),
                    type = CacheType.DYNAMIC,
                    name = "${getOffsetDateTime().toLocalDate()}/${describe.dynamicId}-${index}-${Url(picture.source).getFilename()}"
                )
            }.onFailure {
                logger.warning({ "动态图片下载失败: ${picture.source}" }, it)
            }.let {
                add(it)
            }
        }
    }
}

internal fun BiliVideoInfo.getDuration() = duration.seconds

internal fun BiliVideoInfo.getOffsetDateTime() = timestampToOffsetDateTime(pubdate)

internal fun BiliRoomInfo.getOffsetDateTime() = timestampToOffsetDateTime(liveTime)

internal fun LiveRecord.getRecordUrl() = "https://live.bilibili.com/record/${roomId}"

internal fun BiliVideoInfo.getVideoUrl() = "https://www.bilibili.com/video/${bvid}"

internal fun BiliSearchResult.VideoInfo.getVideoUrl() = "https://www.bilibili.com/video/${bvid}"

internal fun BiliSearchResult.VideoInfo.getOffsetDateTime() = timestampToOffsetDateTime(created)

internal suspend fun BiliSearchResult.VideoInfo.getCover() = getBilibiliImage(
    url = Url(picture),
    type = CacheType.VIDEO,
    name = "${mid}/${bvid}-cover-${Url(picture).getFilename()}",
    refresh = false
)

internal suspend fun BiliRoomSimple.getCover() = getBilibiliImage(
    url = Url(cover),
    type = CacheType.LIVE,
    name = "${roomId}/cover-${Url(cover).getFilename()}",
    refresh = false
)

internal suspend fun BiliVideoInfo.getCover() = getBilibiliImage(
    url = Url(picture),
    type = CacheType.VIDEO,
    name = "${owner.mid}/${bvid}-cover-${Url(picture).getFilename()}",
    refresh = true
)

internal suspend fun LiveRecord.getCover() = getBilibiliImage(
    url = Url(cover),
    type = CacheType.RECORD,
    name = "${roomId}/cover-${Url(cover).getFilename()}",
    refresh = false
)

internal suspend fun LiveRecommend.getCover() = getBilibiliImage(
    url = Url(cover),
    type = CacheType.LIVE,
    name = "${roomId}/cover-${Url(cover).getFilename()}",
    refresh = false
)

private fun noRedirectHttpClient() = HttpClient {
    followRedirects = false
    expectSuccess = false
}

internal suspend fun Url.getLocation() =
    noRedirectHttpClient().use { it.head<HttpMessage>(this).headers[HttpHeaders.Location] }