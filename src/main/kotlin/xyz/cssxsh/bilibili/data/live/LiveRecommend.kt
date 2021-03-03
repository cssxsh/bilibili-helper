package xyz.cssxsh.bilibili.data.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class LiveRecommend(
    @SerialName("area_v2_id")
    val areaId: Int,
    @SerialName("area_v2_name")
    val areaName: String,
    @SerialName("area_v2_parent_id")
    val areaParentId: Int,
    @SerialName("area_v2_parent_name")
    val areaParentName: String,
    @SerialName("broadcast_type")
    private val broadcastType: Int,
    @SerialName("click_callback")
    private val clickCallback: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("face")
    val face: String,
    @SerialName("flag")
    val flag: Int,
    @SerialName("global_order")
    private val globalOrder: Int,
    @SerialName("group_id")
    private val groupId: Int,
    @SerialName("head_box")
    private val headBox: JsonElement? = null,
    @SerialName("head_box_type")
    private val headBoxType: Int,
    @SerialName("is_auto_play")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isAutoPlay: Boolean,
    @SerialName("keyframe")
    private val keyframe: String,
    @SerialName("link")
    val link: String,
    @SerialName("online")
    val online: Int,
    @SerialName("pendant_Info")
    private val pendantInfo: JsonElement? = null,
    @SerialName("roomid")
    val roomId: Long,
    @SerialName("session_id")
    private val sessionId: String,
    @SerialName("show_callback")
    private val showCallback: String,
    @SerialName("special_id")
    private val specialId: Long,
    @SerialName("title")
    val title: String,
    @SerialName("uid")
    val uid: Int,
    @SerialName("uname")
    val uname: String,
    @SerialName("verify")
    private val verify: JsonElement? = null
)