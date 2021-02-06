package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import xyz.cssxsh.bilibili.HttpCookie
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin
import xyz.cssxsh.mirai.plugin.CacheType
import java.io.File

object BilibiliHelperSettings : ReadOnlyPluginConfig("BilibiliHelperConfig") {

    /**
     * 图片缓存位置
     */
    @ValueName("cache_path")
    val cachePath: String by value("BilibiliCache")

    private fun makeCacheDir(): Unit = File(cachePath).run {
        CacheType.values().forEach {
            resolve(it.name).mkdirs()
        }
    }

    @ValueName("init_cookies")
    val initCookies: List<HttpCookie> by value(emptyList())

    @ConsoleExperimentalApi
    fun BilibiliHelperPlugin.reload() {
        makeCacheDir()
        loader.configStorage.load(this, this@BilibiliHelperSettings)
    }
}