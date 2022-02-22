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

internal val DYNAMIC_REGEX = """(?<=t\.bilibili\.com/(?:h5/dynamic/detail/)?|m\.bilibili\.com/dynamic/)(\d+)""".toRegex()

internal val DynamicReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Dynamic(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getDynamicInfo(dynamicId = id.toLong()).dynamic.content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Dynamic(${result.value})信息失败" }, e)
        e.message
    }
}

internal val VIDEO_REGEX = """(?i)(?<!\w)(?:av(\d+)|(BV1[1-9A-NP-Za-km-z]{9}))""".toRegex()

internal val VideoReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Video(${result.value})" }
    try {
        val (aid, bvid) = result.destructured
        message.quote() + when (result.value.first()) {
            'B', 'b' -> client.getVideoInfo(bvid = bvid)
            'A', 'a' -> client.getVideoInfo(aid = aid.toLong())
            else -> throw IllegalArgumentException("未知视频ID(${result.value})")
        }.content(contact = subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Video(${result.value})信息失败" }, e)
        e.message
    }
}

internal val ROOM_REGEX = """(?<=live\.bilibili\.com/(?:h5/)?)(\d+)""".toRegex()

internal val RoomReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Room(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getRoomInfo(roomId = id.toLong()).content(subject)
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
        message.quote() + client.getUserInfo(uid = id.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建User(${result.value})信息失败" }, e)
        e.message
    }
}

internal val SEASON_REGEX = """(?<!\w)ss(\d{4,10})""".toRegex()

internal val SeasonReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Season(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getSeasonInfo(seasonId = id.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Season(${result.value})信息失败" }, e)
        e.message
    }
}

internal val EPISODE_REGEX = """(?<!\w)ep(\d{4,10})""".toRegex()

internal val EpisodeReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Episode(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getEpisodeInfo(episodeId = id.toLong()).content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Episode(${result.value})信息失败" }, e)
        e.message
    }
}

internal val MEDIA_REGEX = """(?<!\w)md(\d{4,10})""".toRegex()

internal val MediaReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Media(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getSeasonMedia(mediaId = id.toLong()).media.content(subject)
    } catch (e: Throwable) {
        logger.warning({ "构建Media(${result.value})信息失败" }, e)
        e.message
    }
}

internal val ARTICLE_REGEX = """(?<!\w)cv(\d{4,10})""".toRegex()

internal val ARTICLE_URL_REGEX = """(?<=bilibili\.com/read/mobile\?id=)(\d+)""".toRegex()

internal val ArticleReplier: MessageReplier = replier@{ result ->
    logger.info { "${sender.render()} 匹配Article(${result.value})" }
    try {
        val (id) = result.destructured
        message.quote() + client.getArticleView(cid = id.toLong()).content(subject)
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

internal val SHORT_LINK_REGEX = """(?:b23\.tv|bili2233\.cn)\\?/([0-9A-z]+)""".toRegex()

internal val ShortLinkReplier: MessageReplier = replier@{ result ->
    val (path) = result.destructured
    logger.info { "${sender.render()} 匹配ShortLink(${result.value}) 尝试跳转" }
    if (VIDEO_REGEX matches path) return@replier null
    if (SEASON_REGEX matches path) return@replier null
    if (EPISODE_REGEX matches path) return@replier null
    if (MEDIA_REGEX matches path) return@replier null
    if (ARTICLE_REGEX matches path) return@replier null
    val location = Url("https://b23.tv/$path").location() ?: return@replier null
    for ((regex, replier) in UrlRepliers) return@replier replier(regex.find(location) ?: continue)
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
        ARTICLE_URL_REGEX to ArticleReplier,
        SHORT_LINK_REGEX to ShortLinkReplier
    )
}