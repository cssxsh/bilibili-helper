package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicBadge(
    @SerialName("bg_color")
    val backgroundColor: String = "",
    @SerialName("color")
    val color: String = "",
    @SerialName("icon_url")
    val iconUrl: String? = null,
    @SerialName("text")
    val text: String
)