package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.mirai.plugin.command.*

private val permission by BiliInfoCommand::permission

internal suspend fun UserInfo.toMessage(contact: Contact) = content.toPlainText() + getFace(contact)

internal suspend fun Video.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun Live.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

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
            runCatching {
                with(client.getUserInfo(uid = uid)) {
                    appendLine("主播: $name#$uid")
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
                    appendLine("链接: $link")
                }
            }.onFailure {
                logger.warning({ "获取[${roomId}]轮播信息失败" }, it)
                appendLine("获取[${roomId}]轮播信息失败")
            }
        }
    }
}

internal suspend fun Media.toMessage(contact: Contact) = content.toPlainText() + getCover(contact)

internal suspend fun SearchResult<*>.toMessage(contact: Contact): Message {
    if (result.isEmpty()) return "搜索结果为空".toPlainText()
    return result.map {
        when (it) {
            is UserInfo -> it.toMessage(contact)
            is Media -> it.toMessage(contact)
            is Video -> it.toMessage(contact)
            else -> "未知类型，请联系开发者".toPlainText()
        } + "\n------------------------\n"
    }.toMessageChain()
}

typealias MessageReplier = suspend MessageEvent.(MatchResult) -> Any?

internal val DynamicReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Dynamic(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        message.quote() + client.getDynamicInfo(result.value.toLong()).dynamic.toMessage(subject)
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
        message.quote() + when (result.value.first()) {
            'B', 'b' -> client.getVideoInfo(result.value)
            'A', 'a' -> client.getVideoInfo(result.value.substring(2).toLong())
            else -> throw IllegalArgumentException("未知视频ID(${result.value})")
        }.toMessage(contact = subject)
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
        message.quote() + client.getRoomInfo(result.value.toLong()).toMessage(subject)
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
        message.quote() + client.getUserInfo(result.value.toLong()).toMessage(subject)
    }.onFailure {
        logger.warning({ "构建User(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val SeasonReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Season(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        message.quote() + client.getSeasonInfo(result.value.toLong()).toMessage(subject)
    }.onFailure {
        logger.warning({ "构建Season(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val EpisodeReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Episode(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        message.quote() + client.getEpisodeInfo(result.value.toLong()).toMessage(subject)
    }.onFailure {
        logger.warning({ "构建Episode(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

internal val MediaReplier: MessageReplier = replier@{ result ->
    logger.info { "[${sender}] 匹配Media(${result.value})" }
    if (permission.testPermission(sender.permitteeId).not()) return@replier null
    runCatching {
        message.quote() + client.getSeasonMedia(result.value.toLong()).media.toMessage(subject)
    }.onFailure {
        logger.warning({ "构建Media(${result.value})信息失败" }, it)
    }.getOrElse {
        it.message
    }
}

private suspend fun Url.location(): String? {
    return client.useHttpClient {
        it.config {
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
    logger.info { "[${sender}] 匹配SHORT_LINK(${result.value}) 尝试跳转" }
    val location = Url("https://b23.tv/${result.value}").location() ?: return@replier null
    for ((regex, replier) in UrlRepliers) {
        val new = regex.find(location) ?: continue
        return@replier replier(new)
    }
}