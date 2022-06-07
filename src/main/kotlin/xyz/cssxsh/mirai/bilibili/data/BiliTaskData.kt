package xyz.cssxsh.mirai.bilibili.data

import net.mamoe.mirai.console.data.*

object BiliTaskData : AutoSavePluginData("BiliTaskData") {
    @ValueDescription("动态订阅信息")
    val dynamic: MutableMap<Long, BiliTask> by value()

    @ValueDescription("视频订阅信息")
    val video: MutableMap<Long, BiliTask> by value()

    @ValueDescription("直播订阅信息")
    val live: MutableMap<Long, BiliTask> by value()

    @ValueDescription("User To Room Map")
    val map: MutableMap<Long, Long> by value()

    @ValueDescription("剧集订阅信息")
    val season: MutableMap<Long, BiliTask> by value()

    @ValueDescription("User To Name")
    val user: MutableMap<Long, String> by value { put(11783021, "哔哩哔哩番剧出差") }
}