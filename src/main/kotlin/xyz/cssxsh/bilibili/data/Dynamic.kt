package xyz.cssxsh.bilibili.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration

interface DynamicCard {
    val card: String
    val detail: DynamicCardDetail
}

interface DynamicCardDetail {
    val type: DynamicType
}

@Serializable
data class BiliDynamicList(
    @SerialName("attentions")
    val attentions: UserAttentions? = null,
    @SerialName("cards")
    val dynamics: List<DynamicInfo> = emptyList(),
//    @SerialName("_gt_")
//    private val gt: Int,
    @SerialName("has_more")
    @Serializable(NumberToBooleanSerializer::class)
    val hasMore: Boolean,
    @SerialName("next_offset")
    val nextOffset: Long
)

@Serializable
data class BiliDynamicInfo(
    @SerialName("attentions")
    val attentions: UserAttentions? = null,
    @SerialName("card")
    val dynamic: DynamicInfo,
//    @SerialName("_gt_")
//    private val gt: Int,
//    @SerialName("result")
//    private val result: Int
)

@Serializable(DynamicType.Serializer::class)
enum class DynamicType(val id: Int) {
    NONE(id = 0),
    REPLY(id = 1),
    PICTURE(id = 2),
    TEXT(id = 4),
    VIDEO(id = 8),
    ARTICLE(id = 64),
    MUSIC(id = 256),
    EPISODE(id = 512),
    DELETE(id = 1024),
    SKETCH(id = 2048),
    BANGUMI(id = 4101),
    LIVE(id = 4200),
    LIVE_END(id = 4308);

    companion object Serializer : KSerializer<DynamicType> {
        override val descriptor: SerialDescriptor
            get() = buildSerialDescriptor("BiliCardTypeSerializer", SerialKind.ENUM)

        override fun serialize(encoder: Encoder, value: DynamicType) = encoder.encodeInt(value.id)

        override fun deserialize(decoder: Decoder): DynamicType = decoder.decodeInt().let { index ->
            requireNotNull(values().find { it.id == index }) { "$index not in ${values().asList()}" }
        }
    }
}

@Serializable
data class DynamicDescribe(
//    @SerialName("acl")
//    private val acl: Int? = null,
    @SerialName("bvid")
    val bvid: String? = null,
    @SerialName("comment")
    val comment: Int = 0,
    @SerialName("dynamic_id")
    val dynamicId: Long,
//    @SerialName("dynamic_id_str")
//    private val dynamicIdString: String? = null,
//    @SerialName("inner_id")
//    private val innerId: Long? = null,
    @SerialName("is_liked")
    @Serializable(NumberToBooleanSerializer::class)
    val isLiked: Boolean = false,
    @SerialName("like")
    val like: Int = 0,
    @SerialName("origin")
    val origin: DynamicDescribe? = null,
    @SerialName("orig_dy_id")
    val originDynamicId: Long? = null,
//    @SerialName("orig_dy_id_str")
//    private val originDynamicIdString: String? = null,
    @SerialName("orig_type")
    val originType: DynamicType? = null,
    @SerialName("previous")
    val previous: DynamicDescribe? = null,
    @SerialName("pre_dy_id")
    val previousDynamicId: Long? = null,
//    @SerialName("pre_dy_id_str")
//    private val previousDynamicIdString: String? = null,
    @SerialName("repost")
    val repost: Int = 0,
//    @SerialName("rid")
//    private val rid: Long? = null,
//    @SerialName("rid_str")
//    private val ridString: String? = null,
//    @SerialName("r_type")
//    private val rType: Int? = null,
//    @SerialName("status")
//    val status: Int? = null,
//    @SerialName("stype")
//    private val statusType: Int? = null,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("type")
    override val type: DynamicType,
    @SerialName("uid")
    val uid: Long,
//    @SerialName("uid_type")
//    private val uidType: Int? = null,
    @SerialName("user_profile")
    val profile: UserProfile? = null,
    @SerialName("view")
    val view: Int? = null
): DynamicCardDetail

@Serializable
data class DynamicArticle(
    @SerialName("act_id")
    val actId: Int,
    @SerialName("apply_time")
    val apply: String,
//    @SerialName("authenMark")
//    private val authenMark: JsonElement?,
    @SerialName("author")
    val author: ArticleAuthor,
    @SerialName("banner_url")
    val bannerUrl: String,
    @SerialName("categories")
    override val categories: List<ArticleCategory>? = null,
    @SerialName("category")
    override val category: ArticleCategory,
    @SerialName("check_time")
    val check: String,
    @SerialName("cover_avid")
    val coverAvid: Int,
    @SerialName("ctime")
    val created: Long,
//    @SerialName("dispute")
//    private val dispute: JsonElement?,
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
//    @SerialName("top_video_info")
//    private val topVideoInfo: JsonElement?,
    @SerialName("words")
    val words: Int
) : Article

@Serializable
data class DynamicEpisode(
    @SerialName("aid")
    val aid: Long,
    @SerialName("apiSeasonInfo")
    val season: SeasonInfo,
    @SerialName("bullet_count")
    val bulletCount: Int,
    @SerialName("cover")
    override val cover: String,
    @SerialName("episode_id")
    override val episodeId: Long,
    @SerialName("index")
    override val index: String,
    @SerialName("index_title")
    override val title: String,
//    @SerialName("item")
//    val item: JsonObject? = null,
    @SerialName("new_desc")
    val description: String,
    @SerialName("online_finish")
    val onlineFinish: Int,
    @SerialName("play_count")
    val playCount: Int,
    @SerialName("reply_count")
    val replyCount: Int,
    @SerialName("url")
    override val share: String
) : Episode

@Serializable
data class SeasonInfo(
//    @SerialName("bgm_type")
//    val type: Int,
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
data class DynamicInfo(
//    @SerialName("activity_infos")
//    private val activityInfos: JsonObject? = null,
    @SerialName("card")
    override val card: String,
    @SerialName("desc")
    override val detail: DynamicDescribe,
    @SerialName("need_refresh")
    @Serializable(NumberToBooleanSerializer::class)
    val needRefresh: Boolean = false,
//    @SerialName("display")
//    private val display: JsonObject,
//    @SerialName("extend_json")
//    private val extendJson: String,
//    @SerialName("extension")
//    private val extension: JsonObject? = null,
//    @SerialName("extra")
//    private val extra: JsonObject? = null
): DynamicCard

@Serializable
data class DynamicLive(
//    @SerialName("area")
//    val area: Int,
//    @SerialName("area_v2_id")
//    val areaId: Int,
//    @SerialName("area_v2_name")
//    val areaName: String,
//    @SerialName("area_v2_parent_id")
//    val areaParentId: Int,
//    @SerialName("area_v2_parent_name")
//    val areaParentName: String,
//    @SerialName("attentions")
//    val attentions: Int,
    @SerialName("background")
    val background: String,
    @SerialName("broadcast_type")
    val broadcastType: Int,
    @SerialName("cover")
    override val cover: String,
    @SerialName("face")
    val face: String,
//    @SerialName("first_live_time")
//    val firstLiveTime: String,
    @SerialName("hidden_status")
    val hiddenStatus: String,
    @SerialName("link")
    override val link: String,
    @SerialName("live_status")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean,
//    @SerialName("live_time")
//    val liveTime: String,
    @SerialName("lock_status")
    val lockStatus: String,
    @SerialName("on_flag")
    val onFlag: Int,
    @SerialName("online")
    override val online: Int,
    @SerialName("room_shield")
    val roomShield: Int,
    @SerialName("room_silent")
    val roomSilent: Int,
    @SerialName("roomid")
    override val roomId: Long,
    @SerialName("round_status")
    @Serializable(NumberToBooleanSerializer::class)
    val roundStatus: Boolean,
    @SerialName("short_id")
    val shortId: Int,
    @SerialName("slide_link")
    val slideLink: String,
    @SerialName("tags")
    val tags: String,
    @SerialName("title")
    override val title: String,
    @SerialName("try_time")
    val tryTime: String,
    @SerialName("uid")
    val uid: Long,
    @SerialName("uname")
    val uname: String,
    @SerialName("user_cover")
    val userCover: String,
    @SerialName("verify")
    val verify: String,
//    @SerialName("virtual")
//    val virtual: Int
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
    val play: Int,
    @SerialName("replyCnt")
    val reply: Int,
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
    val upperAvatar: String
)

@Serializable
data class DynamicPicture(
    @SerialName("item")
    val detail: DynamicPictureDetail,
    @SerialName("user")
    val user: UserSimple,
//    @SerialName("activity_infos")
//    private val activityInfos: JsonObject? = null,
//    @SerialName("extension")
//    private val extension: JsonObject? = null
)

@Serializable
data class DynamicPictureDetail(
    @SerialName("at_control")
    private val atControl: String? = null,
    @SerialName("category")
    val category: String,
//        @SerialName("ctrl")
//        private val ctrl: String? = null,
    @SerialName("description")
    val description: String,
    @SerialName("id")
    val id: Long,
    @SerialName("is_fav")
    @Serializable(NumberToBooleanSerializer::class)
    val isFavourite: Boolean,
    @SerialName("pictures")
    val pictures: List<DynamicPictureInfo>,
    @SerialName("pictures_count")
    val picturesCount: Int,
    @SerialName("reply")
    val reply: Int,
//        @SerialName("role")
//        val role: List<JsonObject>,
//        @SerialName("settings")
//        private val settings: JsonObject,
//        @SerialName("source")
//        private val source: List<JsonObject>,
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
//    @SerialName("origin_extend_json")
//    private val originExtendJson: String,
    @SerialName("origin_user")
    val originUser: UserProfile,
    @SerialName("user")
    val user: UserSimple,
//    @SerialName("activity_infos")
//    private val activityInfos: JsonObject? = null,
//    @SerialName("extension")
//    private val extension: JsonObject? = null,
//    @SerialName("origin_extension")
//    private val originExtension: JsonObject? = null
): DynamicCard

@Serializable
data class DynamicReplyDetail(
    @SerialName("at_uids")
    val atUsers: List<Long> = emptyList(),
    @SerialName("content")
    val content: String,
//        @SerialName("ctrl")
//        private val ctrl: String? = null,
    @SerialName("orig_dy_id")
    val originDynamicId: Long,
    @SerialName("orig_type")
    override val type: DynamicType,
//        @SerialName("pre_dy_id")
//        private val previousDynamicId: Long,
    @SerialName("reply")
    val reply: Int,
//        @SerialName("rp_id")
//        private val replyId: Long,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("uid")
    val uid: Long
): DynamicCardDetail

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
    @SerialName("biz_id")
    val bizId: Int,
    @SerialName("biz_type")
    val bizType: Int,
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
//        @SerialName("ctrl")
//        private val ctrl: String? = null,
    @SerialName("uid")
    val uid: Long
)

@Serializable
data class DynamicText(
    @SerialName("item")
    val detail: DynamicTextDetail,
    @SerialName("user")
    val user: UserSimple,
//    @SerialName("activity_infos")
//    private val activityInfos: JsonObject? = null,
//    @SerialName("extension")
//    private val extension: JsonObject? = null
)

@Serializable
data class DynamicTextDetail(
    @SerialName("at_uids")
    val atUsers: List<Long> = emptyList(),
    @SerialName("content")
    val content: String,
//        @SerialName("ctrl")
//        private val ctrl: String? = null,
//        @SerialName("orig_dy_id")
//        private val originDynamicId: Long,
//        @SerialName("pre_dy_id")
//        private val previousDynamicId: Long,
    @SerialName("reply")
    val reply: Int,
//        @SerialName("rp_id")
//        private val replyId: Long,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("uid")
    val uid: Long
)

@Serializable
data class DynamicVideo(
    @SerialName("aid")
    val aid: Long,
//    @SerialName("attribute")
//    private val attribute: Int? = null,
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
    val duration: Int,
    @SerialName("dynamic")
    val dynamic: String = "",
    @SerialName("jump_url")
    val jumpUrl: String,
//    @SerialName("mission_id")
//    private val missionId: Long? = null,
    @SerialName("owner")
    val owner: VideoOwner,
    @SerialName("pic")
    override val cover: String,
//    @SerialName("player_info")
//    private val playerInfo: JsonObject,
    @SerialName("pubdate")
    val pubdate: Long,
//    @SerialName("rights")
//    private val rights: Map<String, Int> = emptyMap(),
//    @SerialName("state")
//    private val state: Int,
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
        Duration.ofSeconds(duration.toLong()).run { "%02d:%02d".format(toMinutes(), toSecondsPart()) }
    }

    /**
     * AV ID
     */
    override val id: String get() = "av${aid}"
}