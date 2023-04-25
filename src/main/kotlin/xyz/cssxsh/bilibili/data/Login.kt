package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*

@Serializable
data class Qrcode(
    @SerialName("url")
    val url: String,
    @SerialName("qrcode_key")
    val key: String
)

@Serializable
data class QrcodeStatus(
    @SerialName("url")
    val url: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String,
)