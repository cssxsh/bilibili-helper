package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliExtendJson(
    @SerialName("from")
    val from: From,
    @SerialName("like_icon")
    val likeIcon: LikeIcon
) {
    @Serializable
    data class From(
        @SerialName("emoji_type")
        val emojiType: Int,
        @SerialName("from")
        val from: String,
        @SerialName("verify")
        val verify: Map<String, VerifyInfo>
    ) {
        @Serializable
        data class VerifyInfo(
            @SerialName("nv")
            val nv: Int
        )
    }

    @Serializable
    data class LikeIcon(
        @SerialName("action")
        val action: String,
        @SerialName("action_url")
        val actionUrl: String,
        @SerialName("end")
        val end: String,
        @SerialName("end_url")
        val endUrl: String,
        @SerialName("start")
        val start: String,
        @SerialName("start_url")
        val startUrl: String
    )
}