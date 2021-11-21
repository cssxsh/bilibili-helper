package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import net.mamoe.mirai.console.data.*
import xyz.cssxsh.mirai.plugin.*

object BiliCleanerConfig : ReadOnlyPluginConfig("BiliCleanerConfig") {
    @ValueDescription("图片清理间隔，单位小时，默认 1 小时")
    val interval: Map<CacheType, Int> by value(CacheType.values().associateWith { 1 }).withDefault { 1 }

    @ValueDescription("图片过期时间，单位小时，默认 72 小时")
    val expires: Map<CacheType, Int> by value(CacheType.values().associateWith { 72 }).withDefault { 72 }
}