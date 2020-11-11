package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.utils.minutesToMillis

@Serializable
data class BilibiliTaskInfo(
    @SerialName("video_last")
    val videoLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("dynamic_last")
    val dynamicLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("min_interval_millis")
    val minIntervalMillis: Long = 5.minutesToMillis,
    @SerialName("max_interval_millis")
    val maxIntervalMillis: Long = 10.minutesToMillis,
    @SerialName("friends")
    val friends: Set<Long> = emptySet(),
    @SerialName("groups")
    val groups: Set<Long> = emptySet()
)