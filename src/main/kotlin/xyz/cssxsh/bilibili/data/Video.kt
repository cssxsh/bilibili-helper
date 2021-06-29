package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration

interface Video {
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
}

@Serializable
data class BiliVideoInfo(
    @SerialName("aid")
    val aid: Long,
//    @SerialName("attribute")
//    private val attribute: Int? = null,
    @SerialName("bvid")
    override val id: String,
    @SerialName("cid")
    val cid: Int,
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
//    @SerialName("label")
//    private val label: JsonObject? = null,
//    @SerialName("mission_id")
//    private val missionId: Long? = null,
//    @SerialName("no_cache")
//    private val noCache: Boolean,
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
//    @SerialName("rights")
//    private val rights: Map<String, Int>,
    @SerialName("season_id")
    override val seasonId: Long? = null,
    @SerialName("staff")
    val staff: List<VideoStaff> = emptyList(),
    @SerialName("stat")
    override val status: VideoStatus,
//    @SerialName("state")
//    private val state: Int,
//    @SerialName("stein_guide_cid")
//    private val steinGuideCid: Int? = null,
    @SerialName("subtitle")
    val subtitle: VideoSubtitle,
    @SerialName("tid")
    val tid: Int,
    @SerialName("title")
    override val title: String,
    @SerialName("tname")
    val type: String,
    @SerialName("videos")
    val videos: Int,
//    @SerialName("user_garb")
//    private val userGarb: JsonObject? = null,
//    @SerialName("ugc_season")
//    private val ugcSeason: JsonElement? = null,
//    @SerialName("desc_v2")
//    private val descV2: JsonElement? = null,
) : Video {
    override val author: String by owner::name
    override val mid: Long by owner::mid
    override val length: String by lazy {
        Duration.ofSeconds(duration).run { "%02d:%02d".format(toMinutes(), toSecondsPart()) }
    }
}

@Serializable
data class BiliSearchResult(
//    @SerialName("episodic_button")
//    private val episodicButton: JsonObject? = null,
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
)

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
    val face: String,
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
)

@Serializable
data class VideoStatus(
    @SerialName("aid")
    val aid: Long,
    @SerialName("argue_msg")
    val argue: String = "",
    @SerialName("coin")
    val coin: Long,
    @SerialName("danmaku")
    val danmaku: Long,
    @SerialName("dislike")
    @Serializable(NumberToBooleanSerializer::class)
    val dislike: Boolean,
    @SerialName("evaluation")
    val evaluation: String = "",
    @SerialName("favorite")
    val favorite: Long,
    @SerialName("his_rank")
    @Serializable(NumberToBooleanSerializer::class)
    val hisRank: Boolean,
    @SerialName("like")
    val like: Long,
    @SerialName("now_rank")
    val nowRank: Int,
    @SerialName("reply")
    val reply: Long,
    @SerialName("share")
    val share: Long,
    @SerialName("view")
    val view: Long
)

@Serializable
data class VideoStaff(
    @SerialName("face")
    val face: String,
    @SerialName("follower")
    val follower: Int,
//    @SerialName("label_style")
//    private val labelStyle: Int,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("official")
    val official: UserOfficial,
    @SerialName("title")
    val title: String,
//    @SerialName("vip")
//    private val vip: JsonObject
)

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
    val isPay: Boolean,
    @SerialName("is_union_video")
    @Serializable(NumberToBooleanSerializer::class)
    val isUnionVideo: Boolean,
    @SerialName("is_steins_gate")
    @Serializable(NumberToBooleanSerializer::class)
    val isSteinsGate: Boolean,
    @SerialName("is_live_playback")
    @Serializable(NumberToBooleanSerializer::class)
    val isLivePlayback: Boolean,
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
    val tid: Int,
    @SerialName("video_review")
    val videoReview: Int,
    @SerialName("season_id")
    override val seasonId: Long? = null,
    @SerialName("stat")
    override val status: VideoStatus? = null
) : Video

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
    val face: String? = null,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String = "",
)

@Serializable
data class VideoDimension(
    @SerialName("height")
    val height: Int,
    @SerialName("rotate")
    val rotate: Int,
    @SerialName("width")
    val width: Int
)