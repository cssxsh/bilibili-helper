package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicMusic(
    @SerialName("cover")
    val cover: String = "",
    @SerialName("id")
    val id: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("label")
    val label: String,
    @SerialName("title")
    val title: String
)