package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import java.time.*

interface DynamicCard {
    val card: String
    val detail: DynamicCardDetail
}

interface DynamicCardDetail {
    val id: Long
    val type: Int
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

object DynamicType {
    const val NONE = 0
    const val REPLY = 1
    const val PICTURE = 2
    const val TEXT = 4
    const val VIDEO = 8
    const val ARTICLE = 64
    const val MUSIC = 256
    const val EPISODE = 512
    const val DELETE = 1024
    const val SKETCH = 2048
    const val BANGUMI = 4101
    const val LIVE = 4200
    const val LIVE_END = 4308
}

@Serializable
data class DynamicDescribe(
    @SerialName("bvid")
    val bvid: String? = null,
    @SerialName("comment")
    val comment: Int = 0,
    @SerialName("dynamic_id")
    override val id: Long,
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
    val timestamp: Long,
    @SerialName("type")
    override val type: Int,
    @SerialName("uid")
    val uid: Long,
    @SerialName("user_profile")
    val profile: UserProfile? = null,
    @SerialName("view")
    val view: Long = 0
) : DynamicCardDetail

@Serializable
data class EmojiInfo(
    @SerialName("emoji_details")
    val details: List<EmojiDetail> = emptyList()
)

@Serializable
data class DynamicDisplay(
    @SerialName("emoji_info")
    val emoji: EmojiInfo = EmojiInfo(),
    @SerialName("origin")
    val origin: DynamicDisplay? = null
)

@Serializable
data class DynamicInfo(
    @SerialName("card")
    override val card: String,
    @SerialName("desc")
    override val detail: DynamicDescribe,
    @SerialName("display")
    val display: DynamicDisplay
) : DynamicCard

@Serializable
data class DynamicArticle(
    @SerialName("act_id")
    val actId: Int,
    @SerialName("apply_time")
    val apply: String,
    @SerialName("author")
    val author: ArticleAuthor,
    @SerialName("banner_url")
    val banner: String,
    @SerialName("categories")
    override val categories: List<ArticleCategory>? = null,
    @SerialName("category")
    override val category: ArticleCategory,
    @SerialName("check_time")
    val check: String,
    @SerialName("cover_avid")
    val avid: Long = 0,
    @SerialName("ctime")
    val created: Long,
    @SerialName("id")
    override val id: Long,
    @SerialName("image_urls")
    override val images: List<String>,
    @SerialName("is_like")
    val isLike: Boolean,
    @SerialName("list")
    val list: ArticleList?,
    @SerialName("media")
    val media: ArticleMedia,
    @SerialName("origin_image_urls")
    val originImageUrls: List<String>,
    @SerialName("original")
    val original: Int,
    @SerialName("publish_time")
    override val published: Long,
    @SerialName("reprint")
    val reprint: Int,
    @SerialName("state")
    val state: Int,
    @SerialName("stats")
    val status: ArticleStatus,
    @SerialName("summary")
    override val summary: String,
    @SerialName("template_id")
    val templateId: Int,
    @SerialName("title")
    override val title: String,
    @SerialName("words")
    val words: Int
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
    val face: String,
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
    override val online: Long,
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
    val uid: Long,
    @SerialName("uname")
    val uname: String,
    @SerialName("user_cover")
    val avatar: String,
    @SerialName("verify")
    val verify: String
) : Live

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
)

@Serializable
data class DynamicPicture(
    @SerialName("item")
    val detail: DynamicPictureDetail,
    @SerialName("user")
    val user: UserSimple
)

@Serializable
data class DynamicPictureDetail(
    @SerialName("category")
    val category: String,
    @SerialName("description")
    val description: String,
    @SerialName("id")
    val id: Long,
    @SerialName("is_fav")
    @Serializable(NumberToBooleanSerializer::class)
    val isFavourite: Boolean,
    @SerialName("pictures")
    val pictures: List<DynamicPictureInfo>,
    @SerialName("reply")
    val reply: Long,
    @SerialName("title")
    val title: String,
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
    override val detail: DynamicReplyDetail,
    @SerialName("origin")
    override val card: String,
    @SerialName("origin_user")
    val originUser: UserProfile,
    @SerialName("user")
    val user: UserSimple
) : DynamicCard

@Serializable
data class DynamicReplyDetail(
    @SerialName("at_uids")
    val atUsers: List<Long> = emptyList(),
    @SerialName("content")
    val content: String,
    @SerialName("orig_dy_id")
    override val id: Long,
    @SerialName("orig_type")
    override val type: Int,
    @SerialName("reply")
    val reply: Long,
    @SerialName("uid")
    val uid: Long
) : DynamicCardDetail

@Serializable
data class DynamicSketch(
    @SerialName("rid")
    val rid: Long,
    @SerialName("sketch")
    val detail: DynamicSketchDetail,
    @SerialName("user")
    val user: UserSimple,
    @SerialName("vest")
    val vest: DynamicSketchVest
)

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
    val user: UserSimple
)

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
    val tid: Int,
    @SerialName("title")
    override val title: String,
    @SerialName("tname")
    val type: String,
    @SerialName("videos")
    val videos: Int,
    @SerialName("season_id")
    override val seasonId: Long? = null
) : Video {
    override val author: String by owner::name
    override val mid: Long by owner::mid
    override val length: String by lazy {
        with(Duration.ofSeconds(duration)) { "%02d:%02d".format(toMinutes(), toSecondsPart()) }
    }

    /**
     * AV ID
     */
    override val id: String get() = "av${aid}"
}