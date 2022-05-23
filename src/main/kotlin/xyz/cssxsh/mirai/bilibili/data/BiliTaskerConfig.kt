package xyz.cssxsh.mirai.bilibili.data

import net.mamoe.mirai.console.data.*

object BiliTaskerConfig : AutoSavePluginConfig("BiliTaskerConfig") {

    @ValueName("video_forbid_type")
    val videoForbidType: MutableSet<Int> by value()

    @ValueName("video_forbid_pay")
    var videoForbidPay: Boolean by value()

    @ValueName("video_forbid_union")
    var videoForbidUnion: Boolean by value()

    @ValueName("video_forbid_interact")
    var videoForbidInteract: Boolean by value()

    @ValueName("video_forbid_playback")
    var videoForbidPlayback: Boolean by value()

    @ValueName("dynamic_forbid_type")
    val dynamicForbidType: MutableSet<Int> by value()

    @ValueName("dynamic_forbid_regexes")
    val dynamicForbidRegexes: MutableSet<String> by value()
}