package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class BiliUserInfo(
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
    val liveRoom: BiliRoomSimple,
    @SerialName("mid")
    val mid: Long,
    @SerialName("moral")
    val moral: Int,
    @SerialName("name")
    val name: String,
    @SerialName("nameplate")
    val nameplate: UserNameplate,
    @SerialName("official")
    val official: UserOfficial,
    @SerialName("pendant")
    val pendant: UserPendant,
    @SerialName("rank")
    val rank: Int,
    @SerialName("sex")
    val sex: String,
    @SerialName("sign")
    val sign: String,
    @SerialName("user_honour_info")
    private val honour: JsonObject? = null,
    @SerialName("silence")
    @Serializable(with = NumberToBooleanSerializer::class)
    private val silence: Boolean,
    @SerialName("sys_notice")
    private val systemNotice: JsonObject,
    @SerialName("theme")
    private val theme: JsonObject,
    @SerialName("top_photo")
    val topPhoto: String,
    @SerialName("vip")
    private val vip: JsonObject
)