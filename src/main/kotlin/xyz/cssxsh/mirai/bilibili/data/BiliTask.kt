package xyz.cssxsh.mirai.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.mirai.bilibili.*
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