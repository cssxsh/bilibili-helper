package xyz.cssxsh.bilibili.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAttentions(
    @SerialName("uids")
    val users: List<Long>
)