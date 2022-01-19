package xyz.cssxsh.mirai.plugin.data

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
}