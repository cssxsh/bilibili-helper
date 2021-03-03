package xyz.cssxsh.bilibili.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserSimple(
    @SerialName("face")
    val face: String? = null,
    @SerialName("uid")
    val uid: Int,
    @SerialName("uname")
    val uname: String? = null
)