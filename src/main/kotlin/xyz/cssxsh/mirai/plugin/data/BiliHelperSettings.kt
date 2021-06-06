package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object BiliHelperSettings : ReadOnlyPluginConfig("BiliHelperSettings") {
    @ValueDescription("图片缓存位置")
    val cache: String by value("ImageCache")
}