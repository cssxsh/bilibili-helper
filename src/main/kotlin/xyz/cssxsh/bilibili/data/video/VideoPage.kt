package xyz.cssxsh.bilibili.data.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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