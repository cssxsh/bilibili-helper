package xyz.cssxsh.bilibili

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HttpCookie(
    @SerialName("domain")
    val domain: String? = null,
    @SerialName("expirationDate")
    val expirationDate: Double? = null,
    @SerialName("hostOnly")
    val hostOnly: Boolean,
    @SerialName("httpOnly")
    val httpOnly: Boolean,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String? = null,
    @SerialName("sameSite")
    val sameSite: String,
    @SerialName("secure")
    val secure: Boolean,
    @SerialName("session")
    val session: Boolean,
    @SerialName("storeId")
    val storeId: String,
    @SerialName("value")
    val value: String
)