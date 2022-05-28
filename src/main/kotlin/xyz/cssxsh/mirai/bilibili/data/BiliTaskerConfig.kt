package xyz.cssxsh.mirai.bilibili.data

import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.mapKeys
import net.mamoe.mirai.console.permission.*

object BiliTaskerConfig : AutoSavePluginConfig("BiliTaskerConfig") {

    @ValueName("date_time_pattern")
    var pattern: String by value("ISO_OFFSET_DATE_TIME")

    @ValueName("dynamic_forbid_type")
    val dynamicForbidType: MutableSet<Int> by value()

    @ValueName("dynamic_forbid_regexes")
    val dynamicForbidRegexes: MutableSet<String> by value()

    @ValueName("dynamic_sleep")
    val dynamicSleep: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)

    @ValueName("dynamic_at")
    val dynamicAt: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)

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
    val videoSleep: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)

    @ValueName("video_at")
    val videoAt: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)

    @ValueName("season_sleep")
    val seasonSleep: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)

    @ValueName("season_at")
    val seasonAt: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)

    @ValueName("live_sleep")
    val liveSleep: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)

    @ValueName("live_at")
    val liveAt: MutableMap<PermitteeId, BiliInterval> by value<MutableMap<String, BiliInterval>>()
        .mapKeys(oldToNew = AbstractPermitteeId.Companion::parseFromString, newToOld = PermitteeId::asString)
}