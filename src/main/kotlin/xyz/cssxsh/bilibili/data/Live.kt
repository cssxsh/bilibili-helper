package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Live {
    val roomId: Long
    val title: String
    val cover: String
    val online: Int
    val liveStatus: Boolean
    val link: String
}

@Serializable
data class BiliRoomInfo(
    @SerialName("encrypted")
    val encrypted: Boolean,
    @SerialName("hidden_till")
    @Serializable(NumberToBooleanSerializer::class)
    val hiddenTill: Boolean,
    @SerialName("is_hidden")
    val isHidden: Boolean,
    @SerialName("is_locked")
    val isLocked: Boolean,
    @SerialName("is_portrait")
    val isPortrait: Boolean,
    @SerialName("is_sp")
    @Serializable(NumberToBooleanSerializer::class)
    val isSmartPhone: Boolean,
    @SerialName("live_status")
    val status: Int,// 0 未开播 1 开播中 2 轮播中
    @SerialName("live_time")
    val liveTime: Long,
    @SerialName("lock_till")
    @Serializable(NumberToBooleanSerializer::class)
    val lockTill: Boolean,
//    @SerialName("need_p2p")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val needP2P: Boolean,
    @SerialName("pwd_verified")
    val passwordVerified: Boolean,
    @SerialName("room_id")
    val roomId: Long,
//    @SerialName("room_shield")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val roomShield: Boolean,
    @SerialName("short_id")
    val shortId: Int,
//    @SerialName("special_type")
//    private val specialType: Int,
    @SerialName("uid")
    val uid: Long
)

@Serializable
data class BiliRoomSimple(
//    @SerialName("broadcast_type")
//    private val broadcastType: Int,
    @SerialName("cover")
    override val cover: String,
//    @SerialName("link")
//    val link: String? = null,
    @SerialName("liveStatus")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean,
    @SerialName("online")
    override val online: Int,
    @SerialName("online_hidden")
    @Serializable(NumberToBooleanSerializer::class)
    val onlineHidden: Boolean = false,
    @SerialName("roomStatus")
    @Serializable(NumberToBooleanSerializer::class)
    val roomStatus: Boolean,
    @SerialName("roomid")
    override val roomId: Long,
    @SerialName("roundStatus")
    @Serializable(NumberToBooleanSerializer::class)
    val roundStatus: Boolean,
    @SerialName("title")
    override val title: String,
    @SerialName("url")
    override val link: String
) : Live

@Serializable
data class BiliRoundPlayVideo(
    @SerialName("aid")
    val aid: Int,
    @SerialName("bvid")
    val bvid: String? = null,
    @SerialName("bvid_url")
    val link: String? = null,
    @SerialName("cid")
    val cid: Int,
    @SerialName("pid")
    val pid: Int,
    @SerialName("play_time")
    val time: Int,
    @SerialName("play_url")
    val url: String? = null,
    @SerialName("sequence")
    val sequence: Int,
    @SerialName("title")
    val title: String
)

@Serializable
data class BiliLiveOff(
    @SerialName("recommend")
    val recommends: List<LiveRecommend>,
    @SerialName("record_list")
    val records: List<LiveRecord>,
    @SerialName("tips")
    val tips: String
)

@Serializable
data class LiveRecommend(
//    @SerialName("area_v2_id")
//    val areaId: Int,
//    @SerialName("area_v2_name")
//    val areaName: String,
//    @SerialName("area_v2_parent_id")
//    val areaParentId: Int,
//    @SerialName("area_v2_parent_name")
//    val areaParentName: String,
//    @SerialName("broadcast_type")
//    private val broadcastType: Int,
//    @SerialName("click_callback")
//    private val clickCallback: String,
    @SerialName("cover")
    override val cover: String,
    @SerialName("face")
    val face: String,
    @SerialName("flag")
    val flag: Int,
//    @SerialName("global_order")
//    private val globalOrder: Int,
//    @SerialName("group_id")
//    private val groupId: Int,
//    @SerialName("head_box")
//    private val headBox: JsonElement? = null,
//    @SerialName("head_box_type")
//    private val headBoxType: Int,
    @SerialName("is_auto_play")
    @Serializable(NumberToBooleanSerializer::class)
    val isAutoPlay: Boolean,
//    @SerialName("keyframe")
//    private val keyframe: String,
    @SerialName("link")
    override val link: String,
    @SerialName("online")
    override val online: Int,
//    @SerialName("pendant_Info")
//    private val pendantInfo: JsonElement? = null,
    @SerialName("roomid")
    override val roomId: Long,
//    @SerialName("session_id")
//    private val sessionId: String,
//    @SerialName("show_callback")
//    private val showCallback: String,
//    @SerialName("special_id")
//    private val specialId: Long,
    @SerialName("title")
    override val title: String,
    @SerialName("uid")
    val uid: Long,
    @SerialName("uname")
    val uname: String,
    @SerialName("liveStatus")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean = true,
//    @SerialName("verify")
//    private val verify: JsonElement? = null
) : Live

@Serializable
data class LiveRecord(
    @SerialName("bvid")
    val bvid: String,
    @SerialName("cover")
    val cover: String,
//    @SerialName("global_order")
//    private val globalOrder: Int,
    @SerialName("is_sticky")
    @Serializable(NumberToBooleanSerializer::class)
    val isSticky: Boolean,
    @SerialName("rid")
    val roomId: String,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("title")
    val title: String,
    @SerialName("uid")
    val uid: Long,
    @SerialName("uname")
    val uname: String
)