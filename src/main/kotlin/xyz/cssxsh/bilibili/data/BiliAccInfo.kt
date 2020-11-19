package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliAccInfo(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val userData: UserData,
    @SerialName("message")
    val message: String,
    @SerialName("ttl")
    val ttl: Int
) {

    @Serializable
    data class UserData(
        @SerialName("birthday")
        val birthday: String,
        @SerialName("coins")
        val coins: Int,
        @SerialName("face")
        val face: String,
        @SerialName("fans_badge")
        val fansBadge: Boolean,
        @SerialName("is_followed")
        val isFollowed: Boolean,
        @SerialName("jointime")
        val joinTime: Long,
        @SerialName("level")
        val level: Int,
        @SerialName("live_room")
        val liveRoom: BiliLiveRoom,
        @SerialName("mid")
        val mid: Long,
        @SerialName("moral")
        val moral: Int,
        @SerialName("name")
        val name: String,
        @SerialName("nameplate")
        val nameplate: BiliNameplate,
        @SerialName("official")
        val official: BiliOfficial,
        @SerialName("pendant")
        val pendant: BiliPendant,
        @SerialName("rank")
        val rank: Int,
        @SerialName("sex")
        val sex: String,
        @SerialName("sign")
        val sign: String,
        @SerialName("silence")
        val silence: Int,
        @SerialName("sys_notice")
        val systemNotice: JsonElement,
        @SerialName("theme")
        val theme: JsonElement,
        @SerialName("top_photo")
        val topPhoto: String,
        @SerialName("vip")
        val vip: JsonElement
    )
}