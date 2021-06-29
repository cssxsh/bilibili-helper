package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.mirai.plugin.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class BiliTask(
    @SerialName("name")
    val name: String,
    @SerialName("last")
    @Serializable(OffsetDateTimeSerializer::class)
    val last: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("contacts")
    val contacts: Set<Long> = emptySet()
)