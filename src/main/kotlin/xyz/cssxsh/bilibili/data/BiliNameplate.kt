package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliNameplate(
    @SerialName("condition")
    val condition: String,
    @SerialName("image")
    val image: String,
    @SerialName("image_small")
    val imageSmall: String,
    @SerialName("level")
    val level: String,
    @SerialName("name")
    val name: String,
    @SerialName("nid")
    val nid: Long
)