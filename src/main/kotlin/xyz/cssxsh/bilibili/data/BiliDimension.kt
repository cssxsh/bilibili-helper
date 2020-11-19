package xyz.cssxsh.bilibili.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliDimension(
    @SerialName("height")
    val height: Int,
    @SerialName("rotate")
    val rotate: Int,
    @SerialName("width")
    val width: Int
)