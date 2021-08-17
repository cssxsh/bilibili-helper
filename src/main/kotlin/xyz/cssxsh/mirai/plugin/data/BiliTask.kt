package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.*
import xyz.cssxsh.mirai.plugin.*
import java.time.*

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