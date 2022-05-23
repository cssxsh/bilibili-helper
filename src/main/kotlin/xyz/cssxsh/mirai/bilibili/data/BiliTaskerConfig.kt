package xyz.cssxsh.mirai.bilibili.data

import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.permission.*

object BiliTaskerConfig : AutoSavePluginConfig("BiliTaskerConfig") {

    @ValueName("dynamic_forbid_type")
    val dynamicForbidType: MutableSet<Int> by value()

    @ValueName("dynamic_forbid_regexes")
    val dynamicForbidRegexes: MutableSet<String> by value()

    @ValueName("dynamic_sleep")
    val dynamicSleep: MutableMap<AbstractPermitteeId, BiliInterval> by value()

    @ValueName("dynamic_at")
    val dynamicAt: MutableMap<AbstractPermitteeId, BiliInterval> by value()

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

    @ValueName("video_sleep")
    val videoSleep: MutableMap<AbstractPermitteeId, BiliInterval> by value()

    @ValueName("video_at")
    val videoAt: MutableMap<AbstractPermitteeId, BiliInterval> by value()

    @ValueName("season_sleep")
    val seasonSleep: MutableMap<AbstractPermitteeId, BiliInterval> by value()

    @ValueName("season_at")
    val seasonAt: MutableMap<AbstractPermitteeId, BiliInterval> by value()

    @ValueName("live_sleep")
    val liveSleep: MutableMap<AbstractPermitteeId, BiliInterval> by value()

    @ValueName("live_at")
    val liveAt: MutableMap<AbstractPermitteeId, BiliInterval> by value()
}