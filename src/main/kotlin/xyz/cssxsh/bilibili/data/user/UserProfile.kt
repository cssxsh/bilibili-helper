package xyz.cssxsh.bilibili.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserProfile(
    @SerialName("card")
    val card: JsonObject? = null,
    @SerialName("decorate_card")
    val decorate: JsonObject? = null,
    @SerialName("info")
    val user: UserSimple,
    @SerialName("level_info")
    val level: UserLevel? = null,
    @SerialName("pendant")
    val pendant: UserPendant? = null,
    @SerialName("rank")
    val rank: String? = null,
    @SerialName("sign")
    val sign: String,
    @SerialName("vip")
    private val vip: JsonObject? = null
)
