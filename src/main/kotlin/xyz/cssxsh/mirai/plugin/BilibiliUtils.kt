package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.timeoutMillis
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.chromePath
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.deviceName
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.driverUrl
import xyz.cssxsh.mirai.plugin.data.BilibiliHelperSettings.cachePath
import xyz.cssxsh.mirai.plugin.tools.BilibiliChromeDriverTool
import xyz.cssxsh.mirai.plugin.tools.getScreenShot
import java.io.File
import java.net.URL
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.seconds

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

internal fun dynamicTimestamp(id: Long): Long = id / 0x1_0000_0000 + DYNAMIC_START

private fun Url.getFilename() = encodedPath.substring(encodedPath.lastIndexOfAny(listOf("\\", "/")) + 1)

private fun getImage(type: CacheType, name: String): File =
    File(cachePath).resolve(type.name).resolve(name)

internal fun timestampToOffsetDateTime(timestamp: Long) =
    OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.systemDefault())

internal fun timestampToLocalDate(timestamp: Long) =
    timestampToOffsetDateTime(timestamp).toLocalDate()

private suspend fun getBilibiliImage(
    url: Url,
    type: CacheType,
    name: String,
    refresh: Boolean = false
): File = getImage(type = type, name  = name).apply {
    parentFile.mkdirs()
    if (exists().not() || refresh) {
        writeBytes(bilibiliClient.useHttpClient { it.get(url) })
    }
}

internal suspend fun getScreenShot(
    url: String,
    type: CacheType,
    name: String,
    refresh: Boolean = false
): File = getImage(type = type, name  = name).apply {
    parentFile.mkdirs()
    if (exists().not() || refresh) {
        runCatching {
            BilibiliChromeDriverTool(
                remoteAddress = URL(driverUrl),
                chromePath = chromePath,
                deviceName = deviceName
            ).useDriver {
                it.getScreenShot(url, timeoutMillis)
            }
        }.onFailure {
            logger.warning({ "使用ChromeDriver(${driverUrl})失败" }, it)
        }.getOrElse {
            bilibiliClient.useHttpClient {
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

internal suspend fun BiliCardInfo.getScreenShot(refresh: Boolean = false) = getScreenShot(
    url = getDynamicUrl(),
    type = CacheType.DYNAMIC,
    name = "${timestampToLocalDate(describe.timestamp)}/${describe.dynamicId}.png",
    refresh = refresh
)

internal fun BiliCardInfo.toMessageText(): String = buildString {
    when (describe.type) {
        1 -> {
            BILI_JSON.decodeFromString(BiliReplyCard.serializer(), card).let { card ->
                appendLine("${card.user.uname} -> ${card.originUser.info.uname}: ")
                appendLine(card.item.content)
            }
        }
        2 -> {
            BILI_JSON.decodeFromString(BiliPictureCard.serializer(), card).let { card ->
                appendLine("${card.user.name}: ")
                appendLine(card.item.description)
            }
        }
        4 -> {
            BILI_JSON.decodeFromString(BiliTextCard.serializer(), card).let { card ->
                appendLine("${card.user.uname}: ")
                appendLine(card.item.content)
            }
        }
        8 -> {
            BILI_JSON.decodeFromString(BiliVideoCard.serializer(), card).let { card ->
                appendLine("${card.owner.name}: ")
                appendLine(card.title)
            }
        }
        else -> {
            logger.warning("未知类型卡片")
            appendLine("未知类型卡片")
        }
    }
}

internal fun BiliCardInfo.getDynamicUrl() =
    "https://t.bilibili.com/${describe.dynamicId}"

internal suspend fun BiliCardInfo.getImages() = buildList {
    if (describe.type == BiliPictureCard.TYPE) {
        BILI_JSON.decodeFromString(
            deserializer = BiliPictureCard.serializer(),
            string = card
        ).item.pictures.forEachIndexed { index, picture ->
            runCatching {
                getBilibiliImage(
                    url = Url(picture.imageSource),
                    type = CacheType.DYNAMIC,
                    name = "${timestampToLocalDate(describe.timestamp)}/${describe.dynamicId}-${index}-${Url(picture.imageSource).getFilename()}"
                )
            }.onFailure {
                logger.warning({ "动态图片下载失败: ${picture.imageSource}" }, it)
            }.let {
                add(it)
            }
        }
    }
}

internal fun BiliVideoInfo.durationText() =
    duration.seconds.toString()

internal fun BiliLiveRecord.getRecordUrl() =
    "https://live.bilibili.com/record/${rid}"

internal fun BiliLiveRecommend.getLiveUrl() =
    "https://live.bilibili.com${link}"

internal fun BiliVideoInfo.getVideoUrl() =
    "https://www.bilibili.com/video/${bvid}"

internal fun BiliSearchResult.VideoInfo.getVideoUrl() =
    "https://www.bilibili.com/video/${bvid}"

internal suspend fun BiliSearchResult.VideoInfo.getCover() = getBilibiliImage(
    url = Url(picture),
    type = CacheType.VIDEO,
    name ="${bvid}-cover-${Url(picture).getFilename()}",
    refresh = false
)

internal suspend fun BiliLiveRoom.getCover(): File = getBilibiliImage(
    url = Url(cover),
    type = CacheType.LIVE,
    name = "${roomId}-cover-${Url(cover).getFilename()}",
    refresh = false
)

internal suspend fun BiliVideoInfo.getCover(): File = getBilibiliImage(
    url = Url(picture),
    type = CacheType.VIDEO,
    name ="${bvid}-cover-${Url(picture).getFilename()}",
    refresh = true
)

internal suspend fun BiliLiveRecord.getCover(): File = getBilibiliImage(
    url = Url(cover),
    type = CacheType.RECORD,
    name = "${rid}-cover-${Url(cover).getFilename()}",
    refresh = false
)
internal suspend fun BiliLiveRecommend.getCover(): File = getBilibiliImage(
    url = Url(cover),
    type = CacheType.LIVE,
    name = "${roomId}-cover-${Url(cover).getFilename()}",
    refresh = false
)