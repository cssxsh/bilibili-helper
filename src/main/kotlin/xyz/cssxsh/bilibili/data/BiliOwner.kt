package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliOwner(
    @SerialName("face")
    val face: String,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String
)