package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class LikeIcon(
    @SerialName("action_url")
    val actionUrl: String = "",
    @SerialName("end_url")
    val endUrl: String = "",
    @SerialName("id")
    val id: Long = 0,
    @SerialName("start_url")
    val startUrl: String = ""
)