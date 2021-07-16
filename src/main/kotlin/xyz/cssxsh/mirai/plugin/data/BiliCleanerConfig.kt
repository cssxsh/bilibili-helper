package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.PluginDataExtensions.mapKeys
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.mirai.plugin.*

object BiliCleanerConfig : ReadOnlyPluginConfig("BiliCleanerConfig") {
    @ValueDescription("图片清理间隔，单位小时，默认 6 小时")
    val interval: Map<CacheType, Int> by value(CacheType.values().associate { it.name to 6 }).withDefault { 6 }
        .mapKeys(CacheType::valueOf, CacheType::name)
}