package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliCardInfo(
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