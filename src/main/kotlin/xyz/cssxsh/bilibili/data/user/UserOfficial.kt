package xyz.cssxsh.bilibili.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserOfficial(
    @SerialName("desc")
    val description: String,
    @SerialName("role")
    val role: Int,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int
)