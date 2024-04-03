package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.*
import java.time.*

internal inline fun <reified T : Entry> DynamicCard.decode(): T {
    if (decode == null) {
        val entry = try {
            BiliClient.Json.decodeFromString<T>(card)
        } catch (cause : SerializationException) {
            throw IllegalArgumentException("card: ${detail.id}", cause)
        }
        decode = when (entry) {
            is DynamicReply -> entry.copy(detail = detail.origin ?: entry.describe(), display = display)
            is DynamicText -> entry.copy(emoji = display?.emoji)
            is DynamicPicture -> entry.copy(emoji = display?.emoji)
            is DynamicSketch -> entry.copy(emoji = display?.emoji)
            is DynamicVideo -> entry.copy(id = detail.bvid ?: "av${entry.aid}")
            is DynamicLiveRoom -> entry.copy(uname = profile.user.uname)
            else -> entry
        }
    }
    return decode as T
}

sealed interface DynamicCard : Entry, WithDateTime {
    val card: String
    val detail: DynamicDescribe
    val display: DynamicDisplay?
    val profile: UserProfile

    override val datetime: OffsetDateTime

    var decode: Entry?

    fun images(): List<String> = when (detail.type) {
        in DynamicType.DYNAMIC_TYPE_DRAW -> decode<DynamicPicture>().detail.pictures.map { it.source }
        else -> emptyList()
    }

    fun username(): String = when (detail.type) {
        in DynamicType.DYNAMIC_TYPE_PGC -> decode<DynamicEpisode>().season.title
        else -> profile.user.uname
    }

    fun uid() = when (detail.type) {
        in DynamicType.DYNAMIC_TYPE_PGC -> decode<DynamicEpisode>().season.seasonId
        else -> profile.user.uid
    }
}

sealed interface DynamicEmojiContent : Entry {
    val emoji: EmojiInfo?
    val content: String
}

@Serializable
data class BiliDynamicList(
    @SerialName("cards")
    val dynamics: List<DynamicInfo> = emptyList(),
    @SerialName("has_more")
    @Serializable(NumberToBooleanSerializer::class)
    val more: Boolean,
    @SerialName("next_offset")
    val next: Long
)

@Serializable
data class BiliDynamicInfo(
    @SerialName("card")
    val dynamic: DynamicInfo,
)

enum class DynamicType(vararg pairs: Pair<Int, String>) : Map<Int, String> by mapOf(pairs = pairs) {
    DYNAMIC_TYPE_NONE(
        0x0000_0000 to "动态被删除",
        0x0000_0400 to "动态被删除"
    ),
    DYNAMIC_TYPE_FORWARD(
        0x0000_0001 to "转发动态"
    ),
    DYNAMIC_TYPE_DRAW(
        0x0000_0002 to "动态"
    ),
    DYNAMIC_TYPE_WORD(
        0x0000_0004 to "动态"
    ),
    DYNAMIC_TYPE_AV(
        0x0000_0008 to "投稿视频"
    ),
    DYNAMIC_TYPE_ARTICLE(
        0x0000_0040 to "专栏"
    ),
    DYNAMIC_TYPE_MUSIC(
        0x0000_0100 to "音频"
    ),
    DYNAMIC_TYPE_PGC(
        0x0000_0200 to "番剧",
        0x0000_1001 to "番剧",
        0x0000_1002 to "电影",
        0x0000_1003 to "电视剧",
        0x0000_1004 to "国创",
        0x0000_1005 to "纪录片"
    ),
    DYNAMIC_TYPE_COMMON_SQUARE(
        0x0000_0800 to "活动"
    ),
    DYNAMIC_TYPE_COMMON_VERTICAL(
        0x0000_0801 to "2049"
    ),
    DYNAMIC_TYPE_LIVE(
        0x0000_1068 to "直播"
    ),
    DYNAMIC_TYPE_COURSES_SEASON(
        0x0000_10D1 to "4305"
    ),
    DYNAMIC_TYPE_MEDIALIST(
        0x0000_10CC to "收藏"
    ),
    DYNAMIC_TYPE_APPLET(
        0x0000_10CE to "4305"
    ),
    DYNAMIC_TYPE_LIVE_RCMD(
        0x0000_10D4 to "房间"
    ),
    DYNAMIC_TYPE_UGC_SEASON(
        0x0000_10D6 to "合集"
    ),
    DYNAMIC_TYPE_SUBSCRIPTION_NEW(
        0x0000_10D7 to "4311"
    )
    ;

    companion object {
        @JvmStatic
        @JvmName("valueById")
        operator fun invoke(id: Int): DynamicType {
            for (value in values()) {
                if (id in value) return value
            }
            throw IllegalArgumentException("$id for DynamicType")
        }
    }
}

@Serializable
data class DynamicDescribe(
    @SerialName("bvid")
    val bvid: String? = null,
    @SerialName("comment")
    val comment: Int = 0,
    @SerialName("dynamic_id")
    val id: Long = 0,
    @SerialName("is_liked")
    @Serializable(NumberToBooleanSerializer::class)
    val isLiked: Boolean = false,
    @SerialName("like")
    val like: Long = 0,
    @SerialName("origin")
    val origin: DynamicDescribe? = null,
    @SerialName("orig_dy_id")
    val originDynamicId: Long? = null,
    @SerialName("orig_type")
    val originType: Int? = null,
    @SerialName("previous")
    val previous: DynamicDescribe? = null,
    @SerialName("pre_dy_id")
    val previousDynamicId: Long? = null,
    @SerialName("repost")
    val repost: Long = 0,
    @SerialName("timestamp")
    val timestamp: Long = 0,
    @SerialName("type")
    val type: Int = 0,
    @SerialName("uid")
    val uid: Long = 0,
    @SerialName("user_profile")
    val profile: UserProfile = UserProfile.Empty,
    @SerialName("view")
    val view: Long = 0
) {
    companion object {
        val Empty = DynamicDescribe()
    }
}

@Serializable
data class EmojiInfo(
    @SerialName("emoji_details")
    val details: List<EmojiDetail> = emptyList()
)

@Serializable
data class CardInfo(
    @SerialName("add_on_card_show_type")
    val type: Int,
    @SerialName("vote_card")
    val vote: String = ""
)

@Serializable
data class DynamicDisplay(
    @SerialName("emoji_info")
    val emoji: EmojiInfo = EmojiInfo(),
    @SerialName("origin")
    val origin: DynamicDisplay? = null,
    // TODO: add_on_card_info
    @SerialName("add_on_card_info")
    val infos: List<CardInfo> = emptyList()
)

@Serializable
data class DynamicInfo(
    @SerialName("card")
    override val card: String,
    @SerialName("desc")
    override val detail: DynamicDescribe = DynamicDescribe.Empty,
    @SerialName("display")
    override val display: DynamicDisplay? = null
) : DynamicCard, Entry {
    val link get() = "https://t.bilibili.com/${detail.id}"
    val h5 get() = "https://t.bilibili.com/h5/dynamic/detail/${detail.id}"

    @Transient
    override var decode: Entry? = null
    override val profile: UserProfile get() = detail.profile
    override val datetime: OffsetDateTime get() = timestamp(detail.timestamp)
}

@Serializable
data class DynamicArticle(
    @SerialName("author")
    val author: ArticleAuthor,
    @SerialName("categories")
    val categories: List<ArticleCategory>? = null,
    @SerialName("cover_avid")
    val avid: Long = 0,
    @SerialName("ctime")
    val created: Long,
    @SerialName("id")
    override val id: Long,
    @SerialName("image_urls")
    override val images: List<String>,
    @SerialName("origin_image_urls")
    val originImageUrls: List<String>,
    @SerialName("publish_time")
    override val published: Long,
    @SerialName("stats")
    override val status: ArticleStatus,
    @SerialName("summary")
    override val summary: String,
    @SerialName("title")
    override val title: String
) : Article

@Serializable
data class DynamicEpisode(
    @SerialName("apiSeasonInfo")
    val season: SeasonInfo,
    @SerialName("bullet_count")
    val bullet: Long,
    @SerialName("cover")
    override val cover: String,
    @SerialName("episode_id")
    override val episodeId: Long,
    @SerialName("index")
    override val index: String,
    @SerialName("index_title")
    override val title: String,
    @SerialName("new_desc")
    val description: String,
    @SerialName("online_finish")
    val onlineFinish: Int,
    @SerialName("play_count")
    val play: Long,
    @SerialName("reply_count")
    val reply: Long,
    @SerialName("url")
    override val share: String
) : Episode

@Serializable
data class SeasonInfo(
    @SerialName("cover")
    override val cover: String,
    @SerialName("is_finish")
    @Serializable(NumberToBooleanSerializer::class)
    val isFinish: Boolean,
    @SerialName("season_id")
    override val seasonId: Long,
    @SerialName("title")
    override val title: String,
    @SerialName("total_count")
    val total: Long,
    @SerialName("ts")
    val timestamp: Long,
    @SerialName("type_name")
    override val type: String
) : Season

@Serializable
data class DynamicLive(
    @SerialName("background")
    val background: String,
    @SerialName("cover")
    override val cover: String,
    @SerialName("face")
    override val face: String,
    @SerialName("link")
    override val link: String,
    @SerialName("live_status")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean,
    @SerialName("lock_status")
    val lockStatus: String,
    @SerialName("on_flag")
    val onFlag: Int,
    @SerialName("online")
    override val online: Long = 0,
    @SerialName("roomid")
    override val roomId: Long,
    @SerialName("round_status")
    @Serializable(NumberToBooleanSerializer::class)
    val roundStatus: Boolean,
    @SerialName("short_id")
    val shortId: Int,
    @SerialName("tags")
    val tags: String,
    @SerialName("title")
    override val title: String,
    @SerialName("uid")
    override val uid: Long,
    @SerialName("uname")
    override val uname: String,
    @SerialName("user_cover")
    val avatar: String,
    @SerialName("verify")
    val verify: String,
) : Live {
    override val start: OffsetDateTime? get() = null
}

@Serializable
data class DynamicLiveRoom(
    @SerialName("live_play_info")
    val play: LiveRoomInfo,
    @SerialName("live_record_info")
    val record: LiveRecord?,
    @SerialName("style")
    val style: Int,
    @SerialName("type")
    val type: Int,
    @SerialName("uname")
    override val uname: String = ""
) : Live by play

@Serializable
data class LiveRoomInfo(
    @SerialName("area_id")
    val areaId: Int,
    @SerialName("area_name")
    val areaName: String,
    @SerialName("cover")
    override val cover: String,
    @SerialName("link")
    override val link: String,
    @SerialName("live_id")
    val liveId: Long,
    @SerialName("live_screen_type")
    val liveScreenType: Int,
    @SerialName("live_start_time")
    val liveStartTime: Long,
    @SerialName("live_status")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean,
    @SerialName("online")
    override val online: Long,
    @SerialName("parent_area_id")
    val parentAreaId: Int,
    @SerialName("parent_area_name")
    val parentAreaName: String,
    @SerialName("play_type")
    val playType: Int,
    @SerialName("room_id")
    override val roomId: Long,
    @SerialName("room_type")
    val roomType: Int,
    @SerialName("title")
    override val title: String,
    @SerialName("uid")
    override val uid: Long,
    @SerialName("watched_show")
    val watchedShow: String
) : Live {
    override val start: OffsetDateTime get() = timestamp(liveStartTime)
    override val uname: String get() = title
}

@Serializable
data class DynamicMusic(
    @SerialName("author")
    val author: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("ctime")
    val created: Long,
    @SerialName("id")
    val id: Long,
    @SerialName("intro")
    val intro: String,
    @SerialName("playCnt")
    val play: Long,
    @SerialName("replyCnt")
    val reply: Long,
    @SerialName("schema")
    val schema: String,
    @SerialName("title")
    val title: String,
    @SerialName("typeInfo")
    val type: String,
    @SerialName("upId")
    val upId: Long,
    @SerialName("upper")
    val upper: String,
    @SerialName("upperAvatar")
    val avatar: String
) : Entry, Owner, WithDateTime {
    val link get() = "https://www.bilibili.com/audio/au$id"
    override val uid: Long get() = upId
    override val uname: String get() = author
    override val face: String get() = author
    override val datetime: OffsetDateTime get() = timestamp(created / 1_000)
}

@Serializable
data class DynamicPicture(
    @SerialName("item")
    val detail: DynamicPictureDetail,
    @SerialName("user")
    val user: UserSimple,
    @Transient
    override val emoji: EmojiInfo? = null
) : DynamicEmojiContent {
    override val content: String get() = detail.description
}

@Serializable
data class DynamicPictureDetail(
    @SerialName("description")
    val description: String,
    @SerialName("id")
    val id: Long,
    @SerialName("pictures")
    val pictures: List<DynamicPictureInfo>,
    @SerialName("reply")
    val reply: Long,
    @SerialName("upload_time")
    val uploaded: Long
)

@Serializable
data class DynamicPictureInfo(
    @SerialName("img_height")
    val height: Int,
    @SerialName("img_size")
    val size: Double? = null,
    @SerialName("img_src")
    val source: String,
    @SerialName("img_width")
    val width: Int
)

@Serializable
data class DynamicReply(
    @SerialName("item")
    val item: DynamicReplyDetail,
    @SerialName("origin")
    override val card: String = "null",
    @SerialName("origin_user")
    val originUser: UserProfile = UserProfile.Empty,
    @SerialName("user")
    val user: UserSimple,
    @Transient
    override val detail: DynamicDescribe = DynamicDescribe.Empty,
    @Transient
    override val display: DynamicDisplay? = null
) : DynamicCard, DynamicEmojiContent {
    @Transient
    override var decode: Entry? = null

    override val emoji: EmojiInfo? get() = display?.emoji
    override val content get() = item.content
    override val profile: UserProfile get() = originUser
    override val datetime: OffsetDateTime get() = timestamp(detail.timestamp)

    internal fun describe() = DynamicDescribe.Empty.copy(
        id = item.id,
        type = item.type,
        uid = item.uid,
        timestamp = if (item.timestamp != 0L) item.timestamp else dynamictime(id = item.id),
        profile = originUser
    )
}

@Serializable
data class DynamicReplyDetail(
    @SerialName("at_uids")
    val atUsers: List<Long> = emptyList(),
    @SerialName("content")
    val content: String,
    @SerialName("orig_dy_id")
    val id: Long,
    @SerialName("orig_type")
    val type: Int,
    @SerialName("reply")
    val reply: Long,
    @SerialName("timestamp")
    val timestamp: Long = 0,
    @SerialName("uid")
    val uid: Long
)

@Serializable
data class DynamicSketch(
    @SerialName("rid")
    val rid: Long,
    @SerialName("sketch")
    val detail: DynamicSketchDetail,
    @SerialName("user")
    val user: UserSimple,
    @SerialName("vest")
    val vest: DynamicSketchVest,
    @Transient
    override val emoji: EmojiInfo? = null
) : Entry, DynamicEmojiContent {
    val title get() = detail.title
    val link get() = detail.target
    val cover get() = detail.cover
    override val content get() = vest.content
}

@Serializable
data class DynamicSketchDetail(
    @SerialName("cover_url")
    val cover: String,
    @SerialName("desc_text")
    val description: String,
    @SerialName("sketch_id")
    val sketchId: Long,
    @SerialName("target_url")
    val target: String,
    @SerialName("title")
    val title: String
)

@Serializable
data class DynamicSketchVest(
    @SerialName("content")
    val content: String,
    @SerialName("uid")
    val uid: Long
)

@Serializable
data class DynamicText(
    @SerialName("item")
    val detail: DynamicTextDetail,
    @SerialName("user")
    val user: UserSimple,
    @Transient
    override val emoji: EmojiInfo? = null
) : DynamicEmojiContent {
    override val content: String get() = detail.content
}

@Serializable
data class DynamicTextDetail(
    @SerialName("at_uids")
    val atUsers: List<Long> = emptyList(),
    @SerialName("content")
    val content: String,
    @SerialName("reply")
    val reply: Long,
    @SerialName("uid")
    val uid: Long
)

@Serializable
data class DynamicVideo(
    @SerialName("aid")
    val aid: Long,
    @SerialName("bvid")
    override val id: String = "",
    @SerialName("cid")
    val cid: Int,
    @SerialName("ctime")
    override val created: Long,
    @SerialName("desc")
    override val description: String,
    @SerialName("dimension")
    val dimension: VideoDimension,
    @SerialName("duration")
    val duration: Long,
    @SerialName("dynamic")
    val dynamic: String = "",
    @SerialName("jump_url")
    val jumpUrl: String,
    @SerialName("owner")
    val owner: VideoOwner,
    @SerialName("pic")
    override val cover: String,
    @SerialName("pubdate")
    val pubdate: Long,
    @SerialName("stat")
    override val status: VideoStatus,
    @SerialName("tid")
    override val tid: Int,
    @SerialName("title")
    override val title: String,
    @SerialName("tname")
    override val type: String,
    @SerialName("videos")
    val videos: Int,
    @SerialName("season_id")
    override val seasonId: Long? = null,
    @SerialName("rights")
    val rights: VideoRights = VideoRights.Empty
) : Video {
    override val uid: Long get() = owner.mid
    override val uname: String get() = owner.name
    override val author: String get() = owner.name
    override val mid: Long get() = owner.mid
    override val length: String by lazy {
        with(Duration.ofSeconds(duration)) { "%02d:%02d".format(toMinutes(), toSecondsPart()) }
    }

    override val isPay: Boolean get() = rights.pay || rights.ugcPay
    override val isUnionVideo: Boolean get() = rights.isCooperation
    override val isSteinsGate: Boolean get() = rights.isSteinGate
    override val isLivePlayback: Boolean get() = false
}

@Serializable
data class DynamicMediaList(
    @SerialName("cover")
    val cover: String,
    @SerialName("cover_type")
    val coverType: Int,
    @SerialName("fid")
    val fid: Long,
    @SerialName("id")
    val id: Long,
    @SerialName("intro")
    val intro: String = "",
    @SerialName("media_count")
    val count: Int,
    @SerialName("mid")
    val mid: Long,
    @SerialName("sharable")
    val sharable: Boolean,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int,
    @SerialName("upper")
    val upper: VideoOwner
) : Entry, Owner by upper {

    val link get() = "https://www.bilibili.com/medialist/detail/ml${id}"
}