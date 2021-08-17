package xyz.cssxsh.bilibili

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.data.*
import java.time.*
import kotlin.properties.*

internal fun timestamp(sec: Long) = OffsetDateTime.ofInstant(Instant.ofEpochSecond(sec), ZoneOffset.systemDefault())

val UserInfo.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("名称: ${info.name}")
        appendLine("等级: ${info.level}")
        if (info.live.endsWith("/0").not()) {
            appendLine("直播: ${info.live}")
        }
        if (info.sign.isNotBlank()) {
            appendLine("简介: ")
            appendLine(info.sign)
        }
    }
}

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

val DynamicInfo.link get() = "https://t.bilibili.com/${detail.id}"
val DynamicInfo.username get() = detail.profile?.user?.uname ?: "【动态已删除】"
val DynamicInfo.datetime: OffsetDateTime get() = timestamp(detail.timestamp)
val DynamicInfo.head by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("@${info.username} 动态")
        appendLine("时间: ${info.datetime}")
        appendLine("链接: ${info.link}")
    }
}
val DynamicInfo.content get() = head + content()
val DynamicInfo.images get() = images()
val DynamicReply.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("RT @${info.originUser.user.uname}:")
        appendLine(info.detail.content)
        appendLine("<===================>")
        appendLine(info.content())
    }
}
val DynamicEpisode.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("${info.season.type}: ${info.season.title}")
        appendLine("更新: ${info.index} - ${info.title}")
        appendLine("链接: ${info.share}")
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
        appendLine("链接: ${info.link.substringBefore('?')}")
        appendLine("人气: ${info.online}")
    }
}
val BiliRoomInfo.datetime: OffsetDateTime get() = timestamp(liveTime)
val LiveRecord.link get() = "https://live.bilibili.com/record/${roomId}"

val Season.link get() = "https://www.bilibili.com/bangumi/play/ss${seasonId}"
val Media.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("${info.type}: ${info.title}")
        appendLine("评分: ${info.rating?.let { "${it.score}/${it.count}" } ?: "暂无评分"}")
        appendLine("详情: ${info.share}")
        info.new?.let {
            appendLine("最新: ${it.show}")
        }
        appendLine("链接: ${info.link}")
    }
}

val Video.link get() = "https://www.bilibili.com/video/${id}"
val Video.datetime: OffsetDateTime get() = timestamp(created)
val Video.content by ReadOnlyProperty { info, _ ->
    buildString {
        appendLine("视频: ${info.title}")
        appendLine("作者: ${info.author}")
        appendLine("时间: ${info.datetime}")
        appendLine("时长: ${info.length}")
        appendLine("链接: ${info.link}")
        info.seasonId?.let { id ->
            appendLine("剧集: $id")
        }
        info.status?.let { status ->
            appendLine("点赞: ${status.like} 硬币: ${status.coin} 收藏: ${status.favorite}")
            appendLine("弹幕: ${status.danmaku} 评论: ${status.reply} 分享: ${status.share}")
            appendLine("观看: ${status.view} ${if (status.hisRank) "排行: ${status.nowRank}" else ""}")
        }
        if (info.description.isNotBlank()) {
            appendLine("简介: ")
            appendLine(info.description)
        }
    }
}

private inline fun <reified T> DynamicCard.decode(): T = BiliClient.Json.decodeFromString(card)

fun DynamicCard.content(): String = when (detail.type) {
    DynamicType.NONE -> "不支持的类型${detail.type}"
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
    else -> "[${detail.type}] 不支持的类型 请联系开发者"
}

fun DynamicCard.images(): List<String> = when (detail.type) {
    DynamicType.REPLY -> decode<DynamicReply>().images()
    DynamicType.PICTURE -> decode<DynamicPicture>().detail.pictures.map { it.source }
    DynamicType.VIDEO -> decode<DynamicVideo>().cover.let(::listOf)
    DynamicType.ARTICLE -> decode<DynamicArticle>().originImageUrls
    DynamicType.MUSIC -> decode<DynamicMusic>().cover.let(::listOf)
    DynamicType.EPISODE, DynamicType.BANGUMI -> decode<DynamicEpisode>().cover.let(::listOf)
    DynamicType.LIVE -> decode<DynamicLive>().cover.let(::listOf)
    DynamicType.SKETCH -> decode<DynamicSketch>().detail.cover.let(::listOf)
    else -> emptyList()
}

val VIDEO_REGEX = """((av|AV)\d+|(bv|BV)[0-9A-z]{8,12})""".toRegex()

val DYNAMIC_REGEX = """(?<=t\.bilibili\.com/(h5/dynamic/detail/)?)([0-9]+)""".toRegex()

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

internal inline fun <reified T : Any, reified R> reflect() = ReadOnlyProperty<T, R> { thisRef, property ->
    thisRef::class.java.getDeclaredField(property.name).apply { isAccessible = true }.get(thisRef) as R
}
