package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.mirai.plugin.command.*
import java.time.*

private val permission by BiliInfoCommand::permission

internal suspend fun UserInfo.toMessage(contact: Contact) = content.toPlainText() + getFace(contact)

internal suspend fun Video.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun Live.toMessage(contact: Contact, start: OffsetDateTime? = null) = buildMessageChain {
    add(content)
    try {
        val datetime = start ?: client.getRoomInfo(roomId = roomId).datetime
        appendLine("开播时间: $datetime")
    } catch (e: Throwable) {
        appendLine("开播时间: $e")
    }
    add(getCover(contact))
}

internal suspend fun DynamicInfo.toMessage(contact: Contact): Message {
    return (if (SetupSelenium) screenshot(contact) else emoji(contact)) + getImages(contact)
}

internal suspend fun BiliRoomInfo.toMessage(contact: Contact) = buildMessageChain {
    when (status) {
        0 -> {
            appendLine("未开播")
        }
        1 -> {
            appendLine("开播时间: $datetime")
            try {
                with(client.getUserInfo(uid = uid)) {
                    appendLine("主播: $name#$uid")
                    appendLine(liveRoom.content)
                    add(liveRoom.getCover(contact))
                }
            } catch (e: Throwable) {
                logger.warning({ "获取[${uid}]直播间信息失败" }, e)
                appendLine("获取[${uid}]直播间信息失败")
            }
        }
        2 -> {
            appendLine("轮播中")
            try {
                with(client.getRoundPlayVideo(roomId = roomId)) {
                    appendLine("标题: $title")
                    appendLine("链接: $link")
                }
            } catch (e: Throwable) {
                logger.warning({ "获取[${roomId}]轮播信息失败" }, e)
                appendLine("获取[${roomId}]轮播信息失败")
            }
        }
    }
}

internal suspend fun Media.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun Entry.toMessage(contact: Contact): Message {
    return when (this) {
        is UserInfo -> toMessage(contact)
        is Media -> toMessage(contact)
        is Video -> toMessage(contact)
        is Live -> toMessage(contact)
        is DynamicInfo -> toMessage(contact)
        is BiliRoomInfo -> toMessage(contact)
        is SeasonSection -> buildForwardMessage(contact) {
            for (episode in episodes) {
                val video = client.getVideoInfo(episode.aid)
                contact.bot at video.created.toInt() says video.toMessage(contact)
            }
        }
    }
}

internal suspend fun SearchResult<*>.toMessage(contact: Contact): Message {
    if (result.isEmpty()) return "搜索结果为空".toPlainText()
    return result.map { entry -> entry.toMessage(contact) + "\n------------------------\n" }.toMessageChain()
}

typealias MessageReplier = suspend MessageEvent.(MatchResult) -> Any?

internal val DynamicReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Dynamic(${result.value})" }
    if (permission.testPermission(toCommandSender()).not()) return@replier null
    try {
        message.quote() + client.getDynamicInfo(result.value.toLong()).dynamic.toMessage(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Dynamic(${result.value})信息失败" }, e)
        e.message
    }
}

internal val VideoReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Video(${result.value})" }
    if (permission.testPermission(toCommandSender()).not()) return@replier null
    try {
        message.quote() + when (result.value.first()) {
            'B', 'b' -> client.getVideoInfo(result.value)
            'A', 'a' -> client.getVideoInfo(result.value.substring(2).toLong())
            else -> throw IllegalArgumentException("未知视频ID(${result.value})")
        }.toMessage(contact = subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Video(${result.value})信息失败" }, e)
        e.message
    }
}

internal val RoomReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Room(${result.value})" }
    if (permission.testPermission(toCommandSender()).not()) return@replier null
    try {
        message.quote() + client.getRoomInfo(result.value.toLong()).toMessage(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Room(${result.value})信息失败" }, e)
        e.message
    }
}

internal val SpaceReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配User(${result.value})" }
    if (permission.testPermission(toCommandSender()).not()) return@replier null
    try {
        message.quote() + client.getUserInfo(result.value.toLong()).toMessage(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建User(${result.value})信息失败" }, e)
        e.message
    }
}

internal val SeasonReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Season(${result.value})" }
    if (permission.testPermission(toCommandSender()).not()) return@replier null
    try {
        message.quote() + client.getSeasonInfo(result.value.toLong()).toMessage(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Season(${result.value})信息失败" }, e)
        e.message
    }
}

internal val EpisodeReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Episode(${result.value})" }
    if (permission.testPermission(toCommandSender()).not()) return@replier null
    try {
        message.quote() + client.getEpisodeInfo(result.value.toLong()).toMessage(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Episode(${result.value})信息失败" }, e)
        e.message
    }
}

internal val MediaReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Media(${result.value})" }
    if (permission.testPermission(toCommandSender()).not()) return@replier null
    try {
        message.quote() + client.getSeasonMedia(result.value.toLong()).media.toMessage(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Media(${result.value})信息失败" }, e)
        e.message
    }
}

private suspend fun Url.location(): String? {
    return client.useHttpClient { http, _ ->
        http.config {
            followRedirects = false
            expectSuccess = false
        }.head<HttpMessage>(this@location)
    }.headers[HttpHeaders.Location]
}

internal val UrlRepliers by lazy {
    mapOf(
        DYNAMIC_REGEX to DynamicReplier,
        VIDEO_REGEX to VideoReplier,
        ROOM_REGEX to RoomReplier,
        SPACE_REGEX to SpaceReplier,
        SEASON_REGEX to SeasonReplier,
        EPISODE_REGEX to EpisodeReplier,
        MEDIA_REGEX to MediaReplier,
        SHORT_LINK_REGEX to ShortLinkReplier
    )
}

internal val ShortLinkReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配SHORT_LINK(${result.value}) 尝试跳转" }
    val location = Url("https://b23.tv/${result.value}").location() ?: return@replier null
    for ((regex, replier) in UrlRepliers) {
        val new = regex.find(location) ?: continue
        return@replier replier(new)
    }
}