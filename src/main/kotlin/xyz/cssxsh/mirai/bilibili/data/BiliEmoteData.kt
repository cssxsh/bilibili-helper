package xyz.cssxsh.mirai.bilibili.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.util.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

object BiliEmoteData : AutoSavePluginData("BiliEmoteData") {
    @OptIn(ExperimentalSerializationApi::class)
    internal fun dynamic() = this::class.java.getResourceAsStream("dynamic.json").use {
        BiliClient.Json.decodeFromStream<List<EmoteItem>>(requireNotNull(it) { "找不到Emote初始化文件" })
    }

    @OptIn(ExperimentalSerializationApi::class)
    internal fun reply() = this::class.java.getResourceAsStream("reply.json").use {
        BiliClient.Json.decodeFromStream<List<EmoteItem>>(requireNotNull(it) { "找不到Emote初始化文件" })
    }

    @ConsoleExperimentalApi
    override fun shouldPerformAutoSaveWheneverChanged(): Boolean = false

    @ValueDescription("表情数据")
    val dynamic: MutableMap<String, EmoteItem> by value { for (item in dynamic()) put(item.text, item) }

    @ValueDescription("表情数据")
    val reply: MutableMap<String, EmoteItem> by value { for (item in reply()) put(item.text, item) }
}