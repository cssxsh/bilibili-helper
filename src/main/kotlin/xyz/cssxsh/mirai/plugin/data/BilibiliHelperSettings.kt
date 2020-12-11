package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object BilibiliHelperSettings : ReadOnlyPluginConfig("BilibiliHelperConfig") {

    /**
     * 图片缓存位置
     */
    @ValueName("cache_path")
    val cachePath: String by value("ImageCache")
}