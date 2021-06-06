package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.mirai.plugin.tools.*
import java.awt.Image
import java.io.File

internal val logger by BiliHelperPlugin::logger

internal val client by BiliHelperPlugin::client

internal val ImageCache by lazy { File(BiliHelperSettings.cache) }

internal val RemoteWebDriver by BiliHelperPlugin::driver

internal val DeviceName by lazy { SeleniumToolConfig.device }

enum class CacheType {
    DYNAMIC,
    VIDEO,
    LIVE,
    SEASON;
}

private val Url.filename get() = encodedPath.substringAfterLast("/")

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
            path = "${CacheType.DYNAMIC}/${datetime.toLocalDate()}/${describe.dynamicId}.png",
            refresh = refresh
        ).uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取动态${describe.dynamicId}快照失败" }, it)
        content.toPlainText()
    }
}

internal suspend fun DynamicInfo.getImageFiles(contact: Contact) = images.mapIndexed { index, picture ->
    Url(picture).runCatching {
        getWebImage(
            url = this,
            path = "${CacheType.DYNAMIC}/${datetime.toLocalDate()}/${describe.dynamicId}-${index}-${filename}"
        ).uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取动态${describe.dynamicId}图片[${index}]失败" }, it)
        "获取动态${describe.dynamicId}图片[${index}]失败".toPlainText()
    }
}

internal suspend fun Live.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.LIVE}/${roomId}/cover-${filename}").uploadAsImage(contact)
    }.getOrElse {
        logger.warning({ "获取[${roomId}]直播间封面封面失败" }, it)
        "获取[${roomId}]直播间封面失败".toPlainText()
    }
}

internal suspend fun Video.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.VIDEO}/${mid}/${bvid}-cover-${filename}").uploadAsImage(
            contact
        )
    }.getOrElse {
        logger.warning({ "获取[${title}](${bvid})}视频封面失败" }, it)
        "获取[${title}](${bvid})}视频封面失败".toPlainText()
    }
}

internal suspend fun Season.getCover(contact: Contact): Message {
    return Url(cover).runCatching {
        getWebImage(url = this, path = "${CacheType.SEASON}/${seasonId}/cover-${filename}").uploadAsImage(
            contact
        )
    }.getOrElse {
        logger.warning({ "获取[${title}](${seasonId})}剧集封面失败" }, it)
        "获取[${title}](${seasonId})}剧集封面失败".toPlainText()
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