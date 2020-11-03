package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.utils.minutesToMillis
import java.util.*

object BilibiliTaskData : AutoSavePluginConfig("BilibiliTaskData") {
    val tasks: MutableMap<Long, TaskInfo> by value(mutableMapOf())

    @Serializable
    data class TaskInfo(
        @SerialName("video_last")
        val videoLast: Long = Date().time,
        @SerialName("dynamic_last")
        val dynamicLast: Long = Date().time,
        @SerialName("min_interval_millis")
        val minIntervalMillis: Long = 5.minutesToMillis,
        @SerialName("max_interval_millis")
        val maxIntervalMillis: Long = 10.minutesToMillis,
        @SerialName("friends")
        val friends: Set<Long> = emptySet(),
        @SerialName("groups")
        val groups: Set<Long> = emptySet()
    )
}