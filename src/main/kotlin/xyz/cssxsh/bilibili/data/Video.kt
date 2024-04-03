package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.*
import java.time.*

sealed interface Video : Entry, Owner, WithDateTime {
    val title: String
    val author: String
    val description: String
    val mid: Long
    val created: Long
    val length: String
    val id: String
    val cover: String
    val seasonId: Long?
    val status: VideoStatus?
    val tid: Int
    val type: String

    val isPay: Boolean
    val isUnionVideo: Boolean
    val isSteinsGate: Boolean
    val isLivePlayback: Boolean

    val link get() = "https://www.bilibili.com/video/${id}"
    override val datetime: OffsetDateTime get() = timestamp(created)
}

@Serializable
data class BiliVideoInfo(
    @SerialName("aid")
    val aid: Long,
    @SerialName("bvid")
    override val id: String,
    @SerialName("cid")
    val cid: Long? = null,
    @SerialName("copyright")
    val copyright: Int,
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
    @SerialName("owner")
    val owner: VideoOwner,
    @SerialName("pages")
    val pages: List<VideoPage> = emptyList(),
    @SerialName("pic")
    override val cover: String,
    @SerialName("pubdate")
    val pubdate: Long,
    @SerialName("redirect_url")
    val redirect: String? = null,
    @SerialName("season_id")
    override val seasonId: Long? = null,
    @SerialName("staff")
    val staff: List<VideoStaff> = emptyList(),
    @SerialName("stat")
    override val status: VideoStatus,
    @SerialName("subtitle")
    val subtitle: VideoSubtitle? = null,
    @SerialName("tid")
    override val tid: Int,
    @SerialName("title")
    override val title: String,
    @SerialName("tname")
    override val type: String,
    @SerialName("videos")
    val videos: Int,
    @SerialName("rights")
    val rights: VideoRights = VideoRights.Empty
) : Video {
    override val uid: Long get() = owner.mid
    override val uname: String get() = owner.name
    override val author: String get() = owner.name
    override val mid get() = owner.mid
    override val length: String by lazy {
        with(Duration.ofSeconds(duration)) { "%02d:%02d".format(toMinutes(), toSecondsPart()) }
    }

    override val isPay: Boolean get() = rights.pay || rights.ugcPay
    override val isUnionVideo: Boolean get() = rights.isCooperation
    override val isSteinsGate: Boolean get() = rights.isSteinGate
    override val isLivePlayback: Boolean get() = false
}

@Serializable
data class BiliSearchResult(
    @SerialName("list")
    val list: VideoInfoList,
    @SerialName("page")
    val page: VideoSearchPage
)

@Serializable
data class VideoTypeInfo(
    @SerialName("count")
    val count: Int,
    @SerialName("name")
    val name: String,
    @SerialName("tid")
    val tid: Long
)

@Serializable
data class VideoInfoList(
    @SerialName("tlist")
    val types: Map<Int, VideoTypeInfo>? = null,
    @SerialName("vlist")
    val videos: List<VideoSimple>
) {
    init {
        videos.forEach { it.types = types }
    }
}

@Serializable
data class VideoSearchPage(
    @SerialName("count")
    val count: Int,
    @SerialName("pn")
    val num: Int,
    @SerialName("ps")
    val size: Int
)

@Serializable
data class VideoSubtitle(
    @SerialName("allow_submit")
    val allowSubmit: Boolean,
    @SerialName("list")
    val list: List<VideoSubtitleItem>
)

@Serializable
data class VideoSubtitleItem(
    @SerialName("author_mid")
    val mid: Long? = null,
    @SerialName("author")
    val author: VideoSubtitleAuthor? = null,
    @SerialName("id")
    val id: Long,
    @SerialName("lan")
    val language: String,
    @SerialName("lan_doc")
    val languageDocument: String,
    @SerialName("is_lock")
    val isLock: Boolean,
    @SerialName("subtitle_url")
    val subtitleUrl: String
)

@Serializable
data class VideoSubtitleAuthor(
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("sex")
    val sex: String,
    @SerialName("face")
    override val face: String,
    @SerialName("sign")
    val sign: String,
    @SerialName("rank")
    val rank: Int,
    @SerialName("birthday")
    val birthday: Int,
    @SerialName("is_fake_account")
    @Serializable(NumberToBooleanSerializer::class)
    val isFakeAccount: Boolean,
    @SerialName("is_deleted")
    @Serializable(NumberToBooleanSerializer::class)
    val isDeleted: Boolean
) : Owner {
    override val uid: Long get() = mid
    override val uname: String get() = name
}

@Serializable
data class VideoStatus(
    @SerialName("coin")
    val coin: Long,
    @SerialName("danmaku")
    val danmaku: Long,
    @SerialName("dislike")
    @Serializable(NumberToBooleanSerializer::class)
    val dislike: Boolean,
    @SerialName("favorite")
    val favorite: Long,
    @SerialName("his_rank")
    @Serializable(NumberToBooleanSerializer::class)
    val hisRank: Boolean = false,
    @SerialName("like")
    val like: Long,
    @SerialName("now_rank")
    val nowRank: Int = -1,
    @SerialName("reply")
    val reply: Long,
    @SerialName("share")
    val share: Long,
    @SerialName("view")
    val view: Long
) : Entry

@Serializable
data class VideoStaff(
    @SerialName("face")
    override val face: String,
    @SerialName("follower")
    val follower: Int,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("official")
    val official: UserOfficial,
    @SerialName("title")
    val title: String
) : Owner {
    override val uid: Long get() = mid
    override val uname: String get() = name
}

@Serializable
data class VideoSimple(
    @SerialName("aid")
    val aid: Long,
    @SerialName("author")
    override val author: String,
    @SerialName("bvid")
    override val id: String,
    @SerialName("comment")
    val comment: Long,
    @SerialName("copyright")
    val copyright: String,
    @SerialName("created")
    override val created: Long,
    @SerialName("description")
    override val description: String,
    @SerialName("hide_click")
    val hideClick: Boolean,
    @SerialName("is_pay")
    @Serializable(NumberToBooleanSerializer::class)
    override val isPay: Boolean,
    @SerialName("is_union_video")
    @Serializable(NumberToBooleanSerializer::class)
    override val isUnionVideo: Boolean,
    @SerialName("is_steins_gate")
    @Serializable(NumberToBooleanSerializer::class)
    override val isSteinsGate: Boolean,
    @SerialName("is_live_playback")
    @Serializable(NumberToBooleanSerializer::class)
    override val isLivePlayback: Boolean,
    @SerialName("length")
    override val length: String,
    @SerialName("mid")
    override val mid: Long,
    @SerialName("pic")
    override val cover: String,
    @SerialName("play")
    val play: Long,
    @SerialName("review")
    val review: Int,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    override val title: String,
    @SerialName("typeid")
    override val tid: Int,
    @SerialName("video_review")
    val videoReview: Int,
    @SerialName("season_id")
    override val seasonId: Long? = null,
    @SerialName("stat")
    override val status: VideoStatus? = null
) : Video {
    internal var types: Map<Int, VideoTypeInfo>? = null
    override val type: String get() = types?.get(tid)?.name ?: throw NoSuchElementException("video type: $tid")
    override val uid: Long get() = mid
    override val uname: String get() = author
}

@Serializable
data class VideoPage(
    @SerialName("cid")
    val cid: Int,
    @SerialName("dimension")
    val dimension: VideoDimension,
    @SerialName("duration")
    val duration: Int,
    @SerialName("from")
    val from: String,
    @SerialName("page")
    val page: Int,
    @SerialName("part")
    val part: String,
    @SerialName("vid")
    val bvid: String,
    @SerialName("weblink")
    val weblink: String
)

@Serializable
data class VideoOwner(
    @SerialName("face")
    override val face: String? = null,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String = "",
) : Owner {
    override val uid: Long get() = mid
    override val uname: String get() = name
}

@Serializable
data class VideoDimension(
    @SerialName("height")
    val height: Int,
    @SerialName("rotate")
    val rotate: Int,
    @SerialName("width")
    val width: Int
)

@Serializable
data class VideoRights(
    @SerialName("autoplay")
    @Serializable(NumberToBooleanSerializer::class)
    val autoplay: Boolean = false,
    @SerialName("bp")
    @Serializable(NumberToBooleanSerializer::class)
    val bp: Boolean = false,
    @SerialName("clean_mode")
    @Serializable(NumberToBooleanSerializer::class)
    val cleanMode: Boolean = false,
    @SerialName("download")
    @Serializable(NumberToBooleanSerializer::class)
    val download: Boolean = false,
    @SerialName("elec")
    @Serializable(NumberToBooleanSerializer::class)
    val charge: Boolean = false,
    @SerialName("hd5")
    @Serializable(NumberToBooleanSerializer::class)
    val hd5: Boolean = false,
    @SerialName("is_360")
    @Serializable(NumberToBooleanSerializer::class)
    val is360: Boolean = false,
    @SerialName("is_cooperation")
    @Serializable(NumberToBooleanSerializer::class)
    val isCooperation: Boolean = false,
    @SerialName("is_stein_gate")
    @Serializable(NumberToBooleanSerializer::class)
    val isSteinGate: Boolean = false,
    @SerialName("movie")
    @Serializable(NumberToBooleanSerializer::class)
    val movie: Boolean = false,
    @SerialName("no_background")
    @Serializable(NumberToBooleanSerializer::class)
    val noBackground: Boolean = true,
    @SerialName("no_reprint")
    @Serializable(NumberToBooleanSerializer::class)
    val noReprint: Boolean = true,
    @SerialName("no_share")
    @Serializable(NumberToBooleanSerializer::class)
    val noShare: Boolean = true,
    @SerialName("pay")
    @Serializable(NumberToBooleanSerializer::class)
    val pay: Boolean = false,
    @SerialName("ugc_pay")
    @Serializable(NumberToBooleanSerializer::class)
    val ugcPay: Boolean = false,
    @SerialName("ugc_pay_preview")
    @Serializable(NumberToBooleanSerializer::class)
    val ugcPayPreview: Boolean = false
) {
    companion object {
        val Empty = VideoRights()
    }
}