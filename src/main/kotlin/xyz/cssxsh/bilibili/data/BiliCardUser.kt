package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliCardUser(
    @SerialName("face")
    val face: String? = null,
    @SerialName("uid")
    val uid: Int,
    @SerialName("uname")
    val uname: String
)