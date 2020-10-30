package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.utils.minutesToMillis
import java.util.*

object BilibiliTaskData : AutoSavePluginConfig("BilibiliTaskData") {
    val tasks: MutableMap<Long, TaskInfo> by value(mutableMapOf())

    @ValueName("min_interval_millis")
    val minIntervalMillis: Long by value(5.minutesToMillis)

    @ValueName("max_interval_millis")
    val maxIntervalMillis: Long by value(10.minutesToMillis)

    @Serializable
    data class TaskInfo(
        val videoLast: Long = Date().time,
        val dynamicLast: Long = Date().time,
        val friends: Set<Long> = emptySet(),
        val groups: Set<Long> = emptySet()
    )
}