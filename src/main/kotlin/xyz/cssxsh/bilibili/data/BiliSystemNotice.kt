package xyz.cssxsh.bilibili.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class BiliSystemNotice(
    @SerialName("content")
    val content: String,
    @SerialName("id")
    val id: Long,
    @SerialName("url")
    val url: String
)