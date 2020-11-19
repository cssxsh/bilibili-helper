package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliUserProfile(
    @SerialName("card")
    val card: BiliCardInfo,
    @SerialName("info")
    val info: BiliCardUser,
    @SerialName("level_info")
    val levelInfo: BiliLevelInfo,
    @SerialName("pendant")
    val pendant: BiliPendant,
    @SerialName("rank")
    val rank: String,
    @SerialName("sign")
    val sign: String,
    @SerialName("vip")
    val vip: BiliVip,
    @SerialName("decorate_card")
    val decorate_card: JsonElement? = null
)