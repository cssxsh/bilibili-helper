package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliOfficial(
    @SerialName("desc")
    val desc: String,
    @SerialName("role")
    val role: Int,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int
)