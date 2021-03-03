package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.*

@Serializable
data class BilibiliTaskInfo(
    @SerialName("video_last")
    val videoLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("dynamic_last")
    val dynamicLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("name")
    val name: String = "???",
    @SerialName("interval")
    @Serializable(LongRangeSerializer::class)
    val interval: LongRange = (5).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds(),
    @SerialName("contacts")
    val contacts: List<ContactInfo> = emptyList()
)