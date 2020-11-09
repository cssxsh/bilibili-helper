package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object BilibiliTaskData : AutoSavePluginConfig("BilibiliTaskData") {

    @ValueName("tasks")
    val tasks: MutableMap<Long, TaskInfo> by value(mutableMapOf())
}