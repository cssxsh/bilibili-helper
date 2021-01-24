package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliLiveRoom(
    @SerialName("broadcast_type")
    val broadcastType: Int,
    @SerialName("cover")
    val cover: String,
    @SerialName("liveStatus")
    val liveStatus: Int,
    @SerialName("online")
    val online: Int,
    @SerialName("roomStatus")
    val roomStatus: Int,
    @SerialName("roomid")
    val roomId: Long,
    @SerialName("roundStatus")
    val roundStatus: Int,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String
)