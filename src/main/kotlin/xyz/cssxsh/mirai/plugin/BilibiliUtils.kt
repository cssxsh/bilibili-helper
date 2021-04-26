package xyz.cssxsh.mirai.plugin

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.data.dynamic.*
import xyz.cssxsh.bilibili.data.live.*
import xyz.cssxsh.bilibili.data.video.*
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
internal fun timestamp(id: Long): Long = (id shr 32) + DYNAMIC_START

private val Url.filename get() = encodedPath.substringAfterLast("/")

private fun timestampToOffsetDateTime(timestamp: Long) =
    OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.systemDefault())

private suspend fun getBilibiliImage(
    url: Url,
    type: CacheType,
    name: String,
    refresh: Boolean = false
): File = cacheDir.resolve(type.name).resolve(name).apply {
    if (exists().not() || refresh) {
        parentFile.mkdirs()
        writeBytes(client.useHttpClient { it.get(url) })
    }
}

private suspend fun getScreenShot(
    url: String,
    type: CacheType,
    name: String,
    refresh: Boolean = false
): File = cacheDir.resolve(type.name).resolve(name).apply {
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
    url = url,
    type = CacheType.DYNAMIC,
    name = "${datetime.toLocalDate()}/${describe.dynamicId}.png",
    refresh = refresh
)

internal fun DynamicInfo.isText() = when (describe.type) {
    DynamicType.REPLY, DynamicType.PICTURE, DynamicType.TEXT -> true
    else -> false
}

internal fun <T> DynamicInfo.getCardContent(deserializer: DeserializationStrategy<T>): T =
    BILI_JSON.decodeFromString(deserializer = deserializer, string = card)

internal val DynamicInfo.content
    get() = buildString {
        when (describe.type) {
            DynamicType.NONE -> {
                appendLine("不支持的类型${describe.type}")
            }
            DynamicType.REPLY -> {
                with(getCardContent(DynamicReply.serializer())) {
                    appendLine("RT @${originUser.user.uname}: ")
                    appendLine(item.content)
                }
            }
            DynamicType.PICTURE -> {
                with(getCardContent(DynamicPicture.serializer())) {
                    appendLine(item.description)
                }
            }
            DynamicType.TEXT -> {
                with(getCardContent(DynamicText.serializer())) {
                    appendLine(item.content)
                }
            }
            DynamicType.VIDEO -> {
                with(getCardContent(DynamicVideo.serializer())) {
                    appendLine(title)
                    appendLine(description)
                    appendLine(jumpUrl)
                }
            }
            DynamicType.ARTICLE -> {
                with(getCardContent(DynamicArticle.serializer())) {
                    appendLine(title)
                    appendLine(summary)
                }
            }
            DynamicType.MUSIC -> {
                with(getCardContent(DynamicMusic.serializer())) {
                    appendLine(title)
                    appendLine(intro)
                    appendLine(url)
                }
            }
            DynamicType.EPISODE -> {
                with(getCardContent(DynamicEpisode.serializer())) {
                    appendLine("《${info.title}》- $index")
                    appendLine(indexTitle)
                }
            }
            DynamicType.DELETE -> {
                appendLine("源动态已被作者删除")
            }
            DynamicType.SKETCH -> {
                with(getCardContent(DynamicSketch.serializer())) {
                    appendLine(vest.content)
                    appendLine(sketch.title)
                    appendLine(sketch.targetUrl)
                }
            }
            DynamicType.LIVE -> {
                with(getCardContent(DynamicLive.serializer())) {
                    appendLine(title)
                    appendLine(tags)
                    appendLine(link)
                }
            }
            DynamicType.LIVE_END -> {
                appendLine("直播结束了")
            }
        }
    }

internal val DynamicInfo.username get() = describe.profile?.user?.uname ?: "【动态已删除】"

internal val DynamicInfo.datetime get() = timestampToOffsetDateTime(describe.timestamp)

internal val DynamicInfo.images
    get(): List<String> = when (describe.type) {
        DynamicType.NONE, DynamicType.REPLY, DynamicType.TEXT, DynamicType.DELETE, DynamicType.LIVE_END -> {
            emptyList()
        }
        DynamicType.PICTURE -> {
            getCardContent(DynamicPicture.serializer()).item.pictures.map { it.source }
        }
        DynamicType.VIDEO -> {
            getCardContent(DynamicVideo.serializer()).picture.let(::listOf)
        }
        DynamicType.ARTICLE -> {
            getCardContent(DynamicArticle.serializer()).originImageUrls
        }
        DynamicType.MUSIC -> {
            getCardContent(DynamicMusic.serializer()).cover.let(::listOf)
        }
        DynamicType.EPISODE -> {
            getCardContent(DynamicEpisode.serializer()).cover.let(::listOf)
        }
        DynamicType.LIVE -> {
            getCardContent(DynamicLive.serializer()).cover.let(::listOf)
        }
        DynamicType.SKETCH -> {
            getCardContent(DynamicSketch.serializer()).sketch.coverUrl.let(::listOf)
        }
    }

internal suspend fun DynamicInfo.getImageFiles() = images.mapIndexed { index, picture ->
    runCatching {
        getBilibiliImage(
            url = Url(picture),
            type = CacheType.DYNAMIC,
            name = "${datetime.toLocalDate()}/${describe.dynamicId}-${index}-${Url(picture).filename}"
        )
    }
}

internal val BiliVideoInfo.length get() = duration.seconds

internal val BiliVideoInfo.time get() = timestampToOffsetDateTime(pubdate)

internal val BiliRoomInfo.time get() = timestampToOffsetDateTime(liveTime)

internal val VideoSimple.time get() = timestampToOffsetDateTime(created)

internal val DynamicInfo.url get() = "https://t.bilibili.com/${describe.dynamicId}"

internal val BiliVideoInfo.url get() = "https://www.bilibili.com/video/${bvid}"

internal val LiveRecord.url get() = "https://live.bilibili.com/record/${roomId}"

internal val VideoSimple.url get() = "https://www.bilibili.com/video/${bvid}"

internal val DynamicMusic.url get() = "https://www.bilibili.com/audio/au$id"

internal suspend fun VideoSimple.getCover() = getBilibiliImage(
    url = Url(picture),
    type = CacheType.VIDEO,
    name = "${mid}/${bvid}-cover-${Url(picture).filename}",
    refresh = false
)

internal suspend fun BiliRoomSimple.getCover() = getBilibiliImage(
    url = Url(cover),
    type = CacheType.LIVE,
    name = "${roomId}/cover-${Url(cover).filename}",
    refresh = false
)

internal suspend fun BiliVideoInfo.getCover() = getBilibiliImage(
    url = Url(picture),
    type = CacheType.VIDEO,
    name = "${owner.mid}/${bvid}-cover-${Url(picture).filename}",
    refresh = true
)

internal suspend fun LiveRecord.getCover() = getBilibiliImage(
    url = Url(cover),
    type = CacheType.RECORD,
    name = "${roomId}/cover-${Url(cover).filename}",
    refresh = false
)

internal suspend fun LiveRecommend.getCover() = getBilibiliImage(
    url = Url(cover),
    type = CacheType.LIVE,
    name = "${roomId}/cover-${Url(cover).filename}",
    refresh = false
)

private fun noRedirectHttpClient() = HttpClient {
    followRedirects = false
    expectSuccess = false
}

internal suspend fun Url.getLocation() =
    noRedirectHttpClient().use { it.head<HttpMessage>(this).headers[HttpHeaders.Location] }