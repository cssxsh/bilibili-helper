package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*

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

internal val DYNAMIC_REGEX = """(?<=t\.bilibili\.com/(h5/dynamic/detail/)?|m\.bilibili\.com/dynamic/)(\d+)""".toRegex()

internal val DynamicReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Dynamic(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getDynamicInfo(id.toLong()).dynamic.content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Dynamic(${result.value})信息失败" }, e)
        e.message
    }
}

internal val VIDEO_REGEX = """(?i)(?<!\w)(?:av(\d+)|(BV[0-9A-z]{8,12}))""".toRegex()

internal val VideoReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Video(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + when (result.value.first()) {
            'B', 'b' -> client.getVideoInfo(id)
            'A', 'a' -> client.getVideoInfo(id.toLong())
            else -> throw IllegalArgumentException("未知视频ID(${result.value})")
        }.content(contact = subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Video(${result.value})信息失败" }, e)
        e.message
    }
}

internal val ROOM_REGEX = """(?<=live\.bilibili\.com/(h5/)?)(\d+)""".toRegex()

internal val RoomReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Room(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getRoomInfo(id.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Room(${result.value})信息失败" }, e)
        e.message
    }
}

internal val SPACE_REGEX = """(?<=space\.bilibili\.com/|bilibili\.com/space/)(\d+)""".toRegex()

internal val SpaceReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配User(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getUserInfo(id.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建User(${result.value})信息失败" }, e)
        e.message
    }
}

internal val SEASON_REGEX = """(?i)(?<!\w)ss(\d+)""".toRegex()

internal val SeasonReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Season(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getSeasonInfo(id.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Season(${result.value})信息失败" }, e)
        e.message
    }
}

internal val EPISODE_REGEX = """(?i)(?<!\w)eq(\d+)""".toRegex()

internal val EpisodeReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Episode(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getEpisodeInfo(id.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Episode(${result.value})信息失败" }, e)
        e.message
    }
}

internal val MEDIA_REGEX = """(?i)(?<!\w)md(\d+)""".toRegex()

internal val MediaReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Media(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getSeasonMedia(id.toLong()).media.content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Media(${result.value})信息失败" }, e)
        e.message
    }
}

internal val ARTICLE_REGEX = """(?i)(?<!\w)cv(\d+)|(?<=bilibili\.com/read/mobile\?id=)(\d+)""".toRegex()

internal val ArticleReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Article(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getArticleInfo(id.toLong()).last.content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Article(${result.value})信息失败" }, e)
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

internal val SHORT_LINK_REGEX = """(?<=b23\.tv\\?/)[0-9A-z]+""".toRegex()

internal val ShortLinkReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配SHORT_LINK(${result.value}) 尝试跳转" }
    val location = Url("https://b23.tv/${result.value}").location() ?: return@replier null
    for ((regex, replier) in UrlRepliers) {
        val new = regex.find(location) ?: continue
        return@replier replier(new)
    }
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
        ARTICLE_REGEX to ArticleReplier,
        SHORT_LINK_REGEX to ShortLinkReplier
    )
}