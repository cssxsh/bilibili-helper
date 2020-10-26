package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.utils.minutesToMillis
import java.util.*

object BilibiliTaskData : AutoSavePluginConfig("BilibiliTaskData") {
    val video: MutableMap<Long, TaskInfo> by value()

    val live: MutableMap<Long, TaskInfo> by value()

    val minIntervalMillis: Long by value(5.minutesToMillis)

    val maxIntervalMillis: Long by value(10.minutesToMillis)

    @Serializable
    data class TaskInfo(
        val last: Long = Date().time,
        val friends: Set<Long> = emptySet(),
        val groups: Set<Long> = emptySet()
    )
}