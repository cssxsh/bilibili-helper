package xyz.cssxsh.bilibili.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliLiveRecord(
    @SerialName("bvid")
    val bvid: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("global_order")
    val globalOrder: Int,
    @SerialName("is_sticky")
    val isSticky: Int,
    @SerialName("rid")
    val rid: String,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("title")
    val title: String,
    @SerialName("uid")
    val uid: Int,
    @SerialName("uname")
    val uname: String
)