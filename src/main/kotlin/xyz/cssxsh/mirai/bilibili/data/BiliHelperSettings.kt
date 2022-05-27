package xyz.cssxsh.mirai.bilibili.data

import net.mamoe.mirai.console.data.*

object BiliHelperSettings : ReadOnlyPluginConfig("BiliHelperSettings") {
    @ValueDescription("图片缓存位置")
    val cache: String by value("ImageCache")

    @ValueDescription("动态 订阅 输出图片数量，负数表示输出全部")
    val limit: Int by value(16)

    @ValueDescription("API 访问间隔时间，单位秒")
    val api: Long by value(10L)

    @ValueDescription("视频 订阅 访问间隔时间，单位分钟")
    val video: Long by value(10L)

    @ValueDescription("动态 订阅 访问间隔时间，单位分钟")
    val dynamic: Long by value(10L)

    @ValueDescription("直播 订阅 访问间隔时间，单位分钟")
    val live: Long by value(10L)

    @ValueDescription("番剧 订阅 访问间隔时间，单位分钟")
    val season: Long by value(30L)

    @ValueDescription("启动时刷新 last")
    val refresh: Boolean by value(false)

    @ValueDescription("排除自动解析")
    val ban: Set<String> by value(setOf("av1", "av10492"))

    @ValueDescription("转发自动解析")
    val forward: Boolean by value(false)

    @ValueDescription("自动解析同样内容的间隔")
    val interval: Long by value(600_000L)

    @ValueDescription("一次性推送的上限")
    val max: Int by value(3)
}