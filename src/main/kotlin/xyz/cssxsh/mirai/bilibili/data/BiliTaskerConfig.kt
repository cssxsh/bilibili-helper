package xyz.cssxsh.mirai.bilibili.data

import net.mamoe.mirai.console.data.*

object BiliTaskerConfig : ReadOnlyPluginConfig("BiliTaskerConfig") {

    @ValueName("video_forbid_type")
    val videoForbidType: Set<Int> by value()

    @ValueName("video_forbid_pay")
    val videoForbidPay: Boolean by value()

    @ValueName("video_forbid_union")
    val videoForbidUnion: Boolean by value()

    @ValueName("video_forbid_interact")
    val videoForbidInteract: Boolean by value()

    @ValueName("video_forbid_playback")
    val videoForbidPlayback: Boolean by value()

    @ValueName("dynamic_forbid_type")
    val dynamicForbidType: Set<Int> by value(setOf(0))

    @ValueName("dynamic_forbid_regexes")
    val dynamicForbidRegexes: Set<String> by value(setOf("互动抽奖"))
}