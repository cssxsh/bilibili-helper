package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*

@Serializable
data class WbiImages(
    @SerialName("img_url")
    val imgUrl: String = "",
    @SerialName("sub_url")
    val subUrl: String = ""
)