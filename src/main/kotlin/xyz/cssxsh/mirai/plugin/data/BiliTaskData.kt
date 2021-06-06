package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.*

object BiliTaskData : AutoSavePluginData("BiliTaskData") {
    @ValueDescription("动态订阅信息")
    val dynamic: MutableMap<Long, BiliTask> by value(mutableMapOf())

    @ValueDescription("视频订阅信息")
    val video: MutableMap<Long, BiliTask> by value(mutableMapOf())

    @ValueDescription("直播订阅信息")
    val live: MutableMap<Long, BiliTask> by value(mutableMapOf())

    @ValueDescription("剧集订阅信息")
    val season: MutableMap<Long, BiliTask> by value(mutableMapOf())
}