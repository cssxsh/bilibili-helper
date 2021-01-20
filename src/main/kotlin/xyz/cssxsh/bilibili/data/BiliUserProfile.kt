package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliUserProfile(
    @SerialName("card")
    val card: JsonElement? = null,
    @SerialName("info")
    val info: BiliCardUser,
    @SerialName("level_info")
    val levelInfo: BiliLevelInfo? = null,
    @SerialName("pendant")
    val pendant: BiliPendant? = null,
    @SerialName("rank")
    val rank: String? = null,
    @SerialName("sign")
    val sign: String,
    @SerialName("vip")
    val vip: JsonElement? = null,
    @SerialName("decorate_card")
    val decorateCard: JsonElement? = null
)
