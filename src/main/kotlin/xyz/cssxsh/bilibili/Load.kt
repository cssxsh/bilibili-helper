package xyz.cssxsh.bilibili

import kotlinx.serialization.decodeFromString
import xyz.cssxsh.bilibili.data.*
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.properties.ReadOnlyProperty

internal fun timestamp(sec: Long) = OffsetDateTime.ofInstant(Instant.ofEpochSecond(sec), ZoneOffset.systemDefault())

val Article.link get() = "https://www.bilibili.com/read/cv$id"
val Article.datetime: OffsetDateTime get() = timestamp(published)
val Article.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("专栏: ${info.title}")
        appendLine("时间: ${info.datetime}")
        appendLine("链接: ${info.link}")
        appendLine("简介: ${info.summary}")
    }
}

val DynamicInfo.link get() = "https://t.bilibili.com/${describe.dynamicId}"
val DynamicInfo.username get() = describe.profile?.user?.uname ?: "【动态已删除】"
val DynamicInfo.datetime: OffsetDateTime get() = timestamp(describe.timestamp)
val DynamicInfo.head by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("@${info.username} 动态")
        appendLine("时间: ${info.datetime}")
        appendLine("链接: ${info.link}")
    }
}
val DynamicInfo.content get() = head + card.content(describe.type)
val DynamicInfo.images get() = card.images(describe.type)
val DynamicReply.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("RT @${info.originUser.user.uname}:")
        appendLine(info.detail.content)
        // TODO Video Content
        appendLine(info.origin.content(info.detail.originType))
    }
}
val DynamicEpisode.content by ReadOnlyProperty { info, _ ->
    buildString {
        append(info.season.content)
        append((info as Episode).content)
        append(info.description)
    }
}
val DynamicSketch.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("标题: ${info.detail.title}")
        appendLine("链接: ${info.detail.target}")
        appendLine(info.vest.content)
    }
}
val DynamicMusic.link get() = "https://www.bilibili.com/audio/au$id"
val DynamicMusic.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("音乐: ${info.title}")
        appendLine("作者: ${info.author}")
        appendLine("介绍: ${info.intro}")
        appendLine("链接: ${info.link}")
    }
}

val Live.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("房间: ${info.title}")
        appendLine("开播: ${info.liveStatus}")
        appendLine("链接: ${info.link}")
        appendLine("人气: ${info.online}")
    }
}
val BiliRoomInfo.datetime: OffsetDateTime get() = timestamp(liveTime)
val LiveRecord.link get() = "https://live.bilibili.com/record/${roomId}"

val SeasonMedia.link get() = "https://www.bilibili.com/bangumi/play/ss${seasonId}"
val SeasonMedia.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("${info.type}: ${info.title}")
        appendLine("评分: ${info.rating?.score ?: "暂无评分"}")
        appendLine("人数: ${info.rating?.score ?: "人数过少"}")
        appendLine("详情: ${info.share}")
        appendLine("最新: ${info.new.show}")
        appendLine("链接: ${info.link}")
    }
}
val Episode.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("更新: ${info.index} - ${info.title}")
        appendLine("链接: ${info.share}")
    }
}
val Season.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("${info.type}: ${info.title}")
    }
}
val SeasonTimeline.link get() = "https://www.bilibili.com/bangumi/play/ss${seasonId}"

val Video.link get() = "https://www.bilibili.com/video/${bvid}"
val Video.datetime: OffsetDateTime get() = timestamp(created)
val Video.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("视频: ${info.title}")
        appendLine("作者: ${info.author}")
        appendLine("时间: ${info.datetime}")
        appendLine("时长: ${info.length}")
        appendLine("链接: ${info.link}")
    }
}

private inline fun <reified T> String.decode(): T = BiliClient.Json.decodeFromString(this)

private fun String.content(type: DynamicType): String {
    return when (type) {
        DynamicType.NONE -> "不支持的类型${type}"
        DynamicType.REPLY -> decode<DynamicReply>().content
        DynamicType.PICTURE -> decode<DynamicPicture>().detail.description
        DynamicType.TEXT -> decode<DynamicText>().detail.content
        DynamicType.VIDEO -> decode<DynamicVideo>().content
        DynamicType.ARTICLE -> decode<DynamicArticle>().content
        DynamicType.MUSIC -> decode<DynamicMusic>().content
        DynamicType.EPISODE, DynamicType.BANGUMI -> decode<DynamicEpisode>().content
        DynamicType.DELETE -> "源动态已被作者删除"
        DynamicType.SKETCH -> decode<DynamicSketch>().content
        DynamicType.LIVE -> decode<DynamicLive>().content
        DynamicType.LIVE_END -> "直播结束了"
    }
}

private fun String.images(type: DynamicType): List<String> {
    return when (type) {
        DynamicType.NONE, DynamicType.TEXT, DynamicType.DELETE, DynamicType.LIVE_END -> emptyList()
        DynamicType.REPLY -> with(decode<DynamicReply>()) { origin.images(detail.originType) }
        DynamicType.PICTURE -> decode<DynamicPicture>().detail.pictures.map { it.source }
        DynamicType.VIDEO -> decode<DynamicVideo>().cover.let(::listOf)
        DynamicType.ARTICLE -> decode<DynamicArticle>().originImageUrls
        DynamicType.MUSIC -> decode<DynamicMusic>().cover.let(::listOf)
        DynamicType.EPISODE, DynamicType.BANGUMI -> decode<DynamicEpisode>().cover.let(::listOf)
        DynamicType.LIVE -> decode<DynamicLive>().cover.let(::listOf)
        DynamicType.SKETCH -> decode<DynamicSketch>().detail.cover.let(::listOf)
    }
}

val VIDEO_REGEX = """((video/|av|AV)\d+|(bv|BV)[0-9A-z]{10})""".toRegex()

val DYNAMIC_REGEX = """(?<=t\.bilibili\.com/(h5/dynamic/detail/)?)([0-9]{18})""".toRegex()

val ROOM_REGEX = """(?<=live\.bilibili\.com/)(\d+)""".toRegex()

val SHORT_LINK_REGEX = """(?<=b23\.tv\\?/)[0-9A-z]+""".toRegex()

val SPACE_REGEX = """(?<=space\.bilibili\.com/)(\d+)""".toRegex()

val SEASON_REGEX = """(?<=bilibili\.com/bangumi/play/ss)(\d+)""".toRegex()

val EPISODE_REGEX = """(?<=bilibili\.com/bangumi/play/ep)(\d+)""".toRegex()

val MEDIA_REGEX = """(?<=bilibili\.com/bangumi/media/md)(\d+)""".toRegex()

/**
 * 2017-07-01 00:00:00
 */
private const val DYNAMIC_START = 1498838400L

@Suppress("unused")
internal fun dynamictime(id: Long): Long = (id shr 32) + DYNAMIC_START