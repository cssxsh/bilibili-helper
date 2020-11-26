package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.chromePath
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.deviceName
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.driverUrl
import xyz.cssxsh.mirai.plugin.tools.BilibiliChromeDriverTool
import xyz.cssxsh.mirai.plugin.tools.getScreenShot
import java.io.File
import java.net.URL

const val DYNAMIC_DETAIL = "https://t.bilibili.com/h5/dynamic/detail/"

const val IMAGE_CACHE_DIR = "ImageCache"

fun getSuffix(url: String) = url.substring(url.lastIndexOf(".") + 1)

suspend fun getBilibiliImage(url: String, name: String): File =
    File(IMAGE_CACHE_DIR, "${name}.${getSuffix(url)}").apply {
        parentFile.mkdir()
        if (exists().not()) {
            writeBytes(bilibiliClient.useHttpClient { it.get(url) })
        }
    }

suspend fun getScreenShot(url: String, name: String): File = File(IMAGE_CACHE_DIR, "${name}.png").apply {
    parentFile.mkdir()
    runCatching {
        BilibiliChromeDriverTool(
            remoteAddress = URL(driverUrl),
            chromePath = chromePath,
            deviceName = deviceName
        ).useDriver {
            it.getScreenShot(url, BilibiliChromeDriverConfig.timeoutMillis)
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

