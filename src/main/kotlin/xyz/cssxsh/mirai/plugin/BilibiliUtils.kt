package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
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
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

internal val BILI_JSON = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

internal const val DYNAMIC_DETAIL = "https://t.bilibili.com/h5/dynamic/detail/"

internal fun String.getFilename() = substring(lastIndexOfAny(listOf("\\", "/")) + 1)

fun timestampToFormatText(timestamp: Long): String =
    OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.systemDefault()).format(ISO_OFFSET_DATE_TIME)

suspend fun getBilibiliImage(
    url: String,
    name: String,
    refresh: Boolean = false
): File = File(cachePath).resolve("${name}-${url.getFilename()}").apply {
    if (exists().not() || refresh) {
        writeBytes(bilibiliClient.useHttpClient { it.get(url) })
    }
}

suspend fun getScreenShot(
    url: String,
    name: String,
    refresh: Boolean = false
): File = File(cachePath).resolve("${name}.png").apply {
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

suspend fun BiliCardInfo.getScreenShot(refresh: Boolean = false) =
    getScreenShot(url = DYNAMIC_DETAIL + desc.dynamicId, name = "dynamic-${desc.dynamicId}", refresh = refresh)

fun BiliCardInfo.toMessageText(): String = buildString {
    when (desc.type) {
        1 -> {
            BILI_JSON.decodeFromJsonElement(BiliReplyCard.serializer(), card).let { card ->
                appendLine("${card.user.uname} -> ${card.originUser.info.uname}: ")
                appendLine(card.item.content)
            }
        }
        2 -> {
            BILI_JSON.decodeFromJsonElement(BiliPictureCard.serializer(), card).let { card ->
                appendLine("${card.user.name}: ")
                appendLine(card.item.description)
            }
        }
        4 -> {
            BILI_JSON.decodeFromJsonElement(BiliTextCard.serializer(), card).let { card ->
                appendLine("${card.user.uname}: ")
                appendLine(card.item.content)
            }
        }
        8 -> {
            BILI_JSON.decodeFromJsonElement(BiliVideoCard.serializer(), card).let { card ->
                appendLine("${card.owner.name}: ")
                appendLine(card.title)
            }
        }
        else -> {
        }
    }
}

suspend fun BiliCardInfo.getImages(): List<File> = buildList {
    if (desc.type == BiliPictureCard.TYPE) {
        BILI_JSON.decodeFromJsonElement(
            deserializer = BiliPictureCard.serializer(),
            element = card
        ).item.pictures.forEachIndexed { index, picture ->
            runCatching {
                getBilibiliImage(url = picture.imgSrc, name = "dynamic-${desc.dynamicId}-${index}")
            }.onSuccess {
                add(it)
            }.onFailure {
                logger.warning({ "动态图片下载失败: ${picture.imgSrc}" }, it)
            }
        }
    }
}

suspend fun BiliSearchResult.VideoInfo.getCover(): File =
    getBilibiliImage(url = pic, name ="video-${bvId}-cover", refresh = false)

suspend fun BiliLiveRoom.getCover(): File =
    getBilibiliImage(url = cover, name = "live-${roomId}-cover", refresh = false)


suspend fun BiliVideoInfo.VideoData.getCover(): File =
    getBilibiliImage(url = pic, name ="video-${bvId}-cover", refresh = true)
