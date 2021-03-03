package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class DynamicLive(
    @SerialName("area")
    val area: Int,
    @SerialName("area_v2_id")
    val areaId: Int,
    @SerialName("area_v2_name")
    val areaName: String,
    @SerialName("area_v2_parent_id")
    val areaParentId: Int,
    @SerialName("area_v2_parent_name")
    val areaParentName: String,
    @SerialName("attentions")
    val attentions: Int,
    @SerialName("background")
    val background: String,
    @SerialName("broadcast_type")
    val broadcastType: Int,
    @SerialName("cover")
    val cover: String,
    @SerialName("face")
    val face: String,
    @SerialName("first_live_time")
    val firstLiveTime: String, // XXX
    @SerialName("hidden_status")
    val hiddenStatus: String,
    @SerialName("link")
    val link: String,
    @SerialName("live_status")
    @Serializable(with = NumberToBooleanSerializer::class)
    val liveStatus: Boolean,
    @SerialName("live_time")
    val liveTime: String, // XXX
    @SerialName("lock_status")
    val lockStatus: String,
    @SerialName("on_flag")
    val onFlag: Int,
    @SerialName("online")
    val online: Int,
    @SerialName("room_shield")
    val roomShield: Int,
    @SerialName("room_silent")
    val roomSilent: Int,
    @SerialName("roomid")
    val roomId: Int,
    @SerialName("round_status")
    @Serializable(with = NumberToBooleanSerializer::class)
    val roundStatus: Boolean,
    @SerialName("short_id")
    val shortId: Int,
    @SerialName("slide_link")
    val slideLink: String,
    @SerialName("tags")
    val tags: String,
    @SerialName("title")
    val title: String,
    @SerialName("try_time")
    val tryTime: String,
    @SerialName("uid")
    val uid: Int,
    @SerialName("uname")
    val uname: String,
    @SerialName("user_cover")
    val userCover: String,
    @SerialName("verify")
    val verify: String,
    @SerialName("virtual")
    val virtual: Int // XXX
)