package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactInfo(
    @SerialName("id")
    val id: Long,
    @SerialName("bot")
    val bot: Long,
    @SerialName("type")
    val type: ContactType
)