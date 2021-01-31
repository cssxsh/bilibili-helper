package xyz.cssxsh.bilibili.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliRoundPlayVideo(
    @SerialName("aid")
    val aid: Int,
    @SerialName("bvid")
    val bvid: String,
    @SerialName("bvid_url")
    val bvidUrl: String,
    @SerialName("cid")
    val cid: Int,
    @SerialName("pid")
    val pid: Int,
    @SerialName("play_time")
    val playTime: Int,
    @SerialName("play_url")
    val playUrl: String,
    @SerialName("sequence")
    val sequence: Int,
    @SerialName("title")
    val title: String
)