package xyz.cssxsh.bilibili.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliLiveRecommend(
    @SerialName("area_v2_id")
    val areaV2Id: Int,
    @SerialName("area_v2_name")
    val areaV2Name: String,
    @SerialName("area_v2_parent_id")
    val areaV2ParentId: Int,
    @SerialName("area_v2_parent_name")
    val areaV2ParentName: String,
    @SerialName("broadcast_type")
    val broadcastType: Int,
    @SerialName("click_callback")
    val clickCallback: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("face")
    val face: String,
    @SerialName("flag")
    val flag: Int,
    @SerialName("global_order")
    val globalOrder: Int,
    @SerialName("group_id")
    val groupId: Int,
    @SerialName("head_box")
    val headBox: JsonElement? = null,
    @SerialName("head_box_type")
    val headBoxType: Int,
    @SerialName("is_auto_play")
    val isAutoPlay: Int,
    @SerialName("keyframe")
    val keyframe: String,
    @SerialName("link")
    val link: String,
    @SerialName("online")
    val online: Int,
    @SerialName("pendant_Info")
    val pendantInfo: JsonElement? = null,
    @SerialName("roomid")
    val roomId: Int,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("show_callback")
    val showCallback: String,
    @SerialName("special_id")
    val specialId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("uid")
    val uid: Int,
    @SerialName("uname")
    val uname: String,
    @SerialName("verify")
    val verify: JsonElement? = null
)