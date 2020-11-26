package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliAttentions(
    @SerialName("uids")
    val UIDs: List<Long>
)