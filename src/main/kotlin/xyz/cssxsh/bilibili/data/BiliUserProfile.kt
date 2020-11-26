package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliUserProfile(
    @SerialName("card")
    val card: CardInfo,
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
) {
    @Serializable
    data class CardInfo(
        @SerialName("official_verify")
        val officialVerify: OfficialVerify
    ) {

        @Serializable
        data class OfficialVerify(
            @SerialName("desc")
            val desc: String,
            @SerialName("type")
            val type: Int
        )
    }
}
