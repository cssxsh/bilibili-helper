package xyz.cssxsh.bilibili.data.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoOwner(
    @SerialName("face")
    val face: String? = null,
    @SerialName("mid")
    val mid: Int,
    @SerialName("name")
    val name: String? = null,
)