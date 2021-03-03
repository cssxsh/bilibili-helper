package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.bilibili.HttpCookie
import java.io.File

object BilibiliHelperSettings : ReadOnlyPluginConfig("BilibiliHelperConfig") {

    /**
     * 图片缓存位置
     */
    @ValueName("cache_path")
    private val cachePath: String by value("BilibiliCache")

    val cacheDir get() = File(cachePath)

    @ValueName("init_cookies")
    val initCookies: List<HttpCookie> by value(emptyList())
}