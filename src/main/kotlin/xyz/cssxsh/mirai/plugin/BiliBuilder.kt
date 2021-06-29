package xyz.cssxsh.mirai.plugin

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.mirai.plugin.command.BiliInfoCommand

private val permission by BiliInfoCommand::permission

internal suspend fun UserInfo.toMessage(contact: Contact) = content.toPlainText() + getFace(contact)

internal suspend fun Video.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun Live.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun DynamicInfo.toMessage(contact: Contact): Message {
    return (if (SetupSelenium) getScreenshot(contact) else content.toPlainText()) + getImageFiles(contact)
}

internal suspend fun BiliRoomInfo.toMessage(contact: Contact) = buildMessageChain {
    when (status) {
        0 -> {
            appendLine("未开播")
        }
        1 -> {
            appendLine("开播时间: $datetime")
            runCatching {
                with(client.getUserInfo(mid = uid)) {
                    appendLine("主播: $name")
                    add(liveRoom.toMessage(contact))
                }
            }.onFailure {
                logger.warning({ "获取[${uid}]直播间信息失败" }, it)
                appendLine("获取[${uid}]直播间信息失败")
            }
        }
        2 -> {
            appendLine("轮播中")
            runCatching {
                with(client.getRoundPlayVideo(roomId = roomId)) {
                    appendLine("标题: $title")
                    appendLine("链接: $url")
                }
            }.onFailure {
                logger.warning({ "获取[${roomId}]轮播信息失败" }, it)
                appendLine("获取[${roomId}]轮播信息失败")
            }
        }
    }
}

internal suspend fun SeasonMedia.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun SearchSeason.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun Episode.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

typealias MessageReplier = suspend MessageEvent.(MatchResult) -> Any?

internal val DynamicReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Dynamic(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        client.getDynamicInfo(result.value.toLong()).dynamic.toMessage(subject) + message.quote()
    }.onFailure {
        logger.warning({ "构建Dynamic(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val VideoReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Video(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        when (result.value.first()) {
            'B', 'b' -> client.getVideoInfo(result.value)
            'A', 'a' -> client.getVideoInfo(result.value.substring(2).toLong())
            else -> throw IllegalArgumentException("未知视频ID(${result.value})")
        }.toMessage(contact = subject) + message.quote()
    }.onFailure {
        logger.warning({ "构建Video(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val RoomReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Room(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        client.getRoomInfo(result.value.toLong()).toMessage(subject) + message.quote()
    }.onFailure {
        logger.warning({ "构建Room(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val SpaceReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配User(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        client.getUserInfo(result.value.toLong()).toMessage(subject) + message.quote()
    }.onFailure {
        logger.warning({ "构建User(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val SeasonReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Season(${result.value})" }
    // if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        client.getSeasonSection(result.value.toLong()).mainSection.episodes.first().toMessage(subject) + message.quote()
    }.onFailure {
        logger.warning({ "构建Room(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val EpisodeReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Episode(${result.value})" }
    null
    // if (permission.testPermission(sender.permitteeId).not()) return@replier null
    // TODO
}

internal val MediaReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Media(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        client.getSeasonMedia(result.value.toLong()).media.toMessage(subject) + message.quote()
    }.onFailure {
        logger.warning({ "构建Room(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

private fun noRedirect() = HttpClient {
    followRedirects = false
    expectSuccess = false
}

private suspend fun Url.getLocation() = noRedirect().use { it.head<HttpMessage>(this) }.headers[HttpHeaders.Location]

internal val Repliers by lazy {
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
    logger.info { "[${sender}] 匹配SHORT_LINK(${result.value}) 尝试跳转" }
    val location = Url("https://b23.tv/${result.value}").getLocation() ?: return@replier null
    for ((regex, replier) in Repliers) {
        val new = regex.find(location) ?: continue
        return@replier replier(new)
    }
}