package xyz.cssxsh.bilibili.data.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDimension(
    @SerialName("height")
    val height: Int,
    @SerialName("rotate")
    val rotate: Int,
    @SerialName("width")
    val width: Int
)