package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.PluginDataExtensions.mapKeys
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import net.mamoe.mirai.console.data.*
import xyz.cssxsh.mirai.plugin.*

object BiliCleanerConfig : ReadOnlyPluginConfig("BiliCleanerConfig") {
    @ValueDescription("图片清理间隔，单位小时，默认 1 小时")
    val interval: Map<CacheType, Int> by value(CacheType.values().associate { it.name to 1 }).withDefault { 1 }
        .mapKeys(CacheType::valueOf, CacheType::name)

    @ValueDescription("图片过期时间，单位小时，默认 72 小时")
    val expires: Map<CacheType, Int> by value(CacheType.values().associate { it.name to 72 }).withDefault { 72 }
        .mapKeys(CacheType::valueOf, CacheType::name)
}