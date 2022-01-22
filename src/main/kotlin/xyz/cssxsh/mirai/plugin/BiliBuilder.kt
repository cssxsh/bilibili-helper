@file:OptIn(ConsoleExperimentalApi::class)

package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.SearchResult
import xyz.cssxsh.bilibili.data.SeasonSection

internal suspend fun SeasonSection.toMessage(contact: Contact) = buildForwardMessage(contact) {
    displayStrategy = object : ForwardMessage.DisplayStrategy {
        override fun generateTitle(forward: RawForwardMessage): String = title
    }
    for (episode in episodes) {
        val video = client.getVideoInfo(episode.aid)
        contact.bot at video.created.toInt() says video.content(contact)
    }
}

internal suspend fun SearchResult<*>.toMessage(contact: Contact): Message {
    if (result.isEmpty()) return "搜索结果为空".toPlainText()
    return result.map { entry -> entry.content(contact) + "\n------------------------\n" }.toMessageChain()
}

typealias MessageReplier = suspend MessageEvent.(MatchResult) -> Any?

internal val DynamicReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Dynamic(${result.value})" }
    try {
        message.quote() + client.getDynamicInfo(result.value.toLong()).dynamic.content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Dynamic(${result.value})信息失败" }, e)
        e.message
    }
}

internal val VideoReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Video(${result.value})" }
    try {
        message.quote() + when (result.value.first()) {
            'B', 'b' -> client.getVideoInfo(result.value)
            'A', 'a' -> client.getVideoInfo(result.value.substring(2).toLong())
            else -> throw IllegalArgumentException("未知视频ID(${result.value})")
        }.content(contact = subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Video(${result.value})信息失败" }, e)
        e.message
    }
}

internal val RoomReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Room(${result.value})" }
    try {
        message.quote() + client.getRoomInfo(result.value.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Room(${result.value})信息失败" }, e)
        e.message
    }
}

internal val SpaceReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配User(${result.value})" }
    try {
        message.quote() + client.getUserInfo(result.value.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建User(${result.value})信息失败" }, e)
        e.message
    }
}

internal val SeasonReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Season(${result.value})" }
    try {
        message.quote() + client.getSeasonInfo(result.value.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Season(${result.value})信息失败" }, e)
        e.message
    }
}

internal val EpisodeReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Episode(${result.value})" }
    try {
        message.quote() + client.getEpisodeInfo(result.value.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Episode(${result.value})信息失败" }, e)
        e.message
    }
}

internal val MediaReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Media(${result.value})" }
    try {
        message.quote() + client.getSeasonMedia(result.value.toLong()).media.content(subject)
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