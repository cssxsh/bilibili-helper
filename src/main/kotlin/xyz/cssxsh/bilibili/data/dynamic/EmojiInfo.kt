package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class EmojiInfo(
    @SerialName("icon_url")
    val iconUrl: String = "",
    @SerialName("size")
    val size: Int = 0,
    @SerialName("text")
    val text: String = "",
    @SerialName("type")
    val type: Int = 0
)