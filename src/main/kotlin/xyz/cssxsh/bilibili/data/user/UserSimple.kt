package xyz.cssxsh.bilibili.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSimple(
    @SerialName("face")
    val face: String? = null,
    @SerialName("uid")
    val uid: Long,
    @SerialName("uname")
    val uname: String? = null
)