package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@Serializable
data class BilibiliTaskInfo(
    @SerialName("video_last")
    val videoLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("dynamic_last")
    val dynamicLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("min_interval_millis")
    val minIntervalMillis: Long = (5).minutes.toLongMilliseconds(),
    @SerialName("max_interval_millis")
    val maxIntervalMillis: Long = (10).minutes.toLongMilliseconds(),
    @SerialName("friends")
    val friends: Set<Long> = emptySet(),
    @SerialName("groups")
    val groups: Set<Long> = emptySet()
) {
    fun getInterval() = minIntervalMillis..maxIntervalMillis
}