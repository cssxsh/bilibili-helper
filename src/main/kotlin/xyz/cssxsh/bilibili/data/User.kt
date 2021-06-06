package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val joined: Long,
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
//    @SerialName("user_honour_info")
//    private val honour: JsonObject? = null,
//    @SerialName("silence")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val silence: Boolean,
//    @SerialName("sys_notice")
//    private val systemNotice: JsonObject,
//    @SerialName("theme")
//    private val theme: JsonObject,
    @SerialName("top_photo")
    val topPhoto: String,
//    @SerialName("vip")
//    private val vip: JsonObject
)

@Serializable
data class UserNameplate(
    @SerialName("condition")
    val condition: String,
    @SerialName("image")
    val image: String,
    @SerialName("image_small")
    val imageSmall: String,
    @SerialName("level")
    val level: String,
    @SerialName("name")
    val name: String,
    @SerialName("nid")
    val nid: Long
)

@Serializable
data class UserPendant(
    @SerialName("expire")
    val expire: Int,
    @SerialName("image")
    val image: String,
    @SerialName("image_enhance")
    val imageEnhance: String = "",
    @SerialName("image_enhance_frame")
    val imageEnhanceFrame: String = "",
    @SerialName("name")
    val name: String,
    @SerialName("pid")
    val pid: Long
)

@Serializable
data class UserProfile(
//    @SerialName("card")
//    val card: JsonObject? = null,
//    @SerialName("decorate_card")
//    val decorate: JsonObject? = null,
    @SerialName("info")
    val user: UserSimple,
    @SerialName("level_info")
    val level: UserLevel? = null,
    @SerialName("pendant")
    val pendant: UserPendant? = null,
    @SerialName("rank")
    val rank: String? = null,
    @SerialName("sign")
    val sign: String,
//    @SerialName("vip")
//    private val vip: JsonObject? = null
)

@Serializable
data class UserOfficial(
    @SerialName("desc")
    val description: String,
    @SerialName("role")
    val role: Int,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int
)

@Serializable
data class UserSimple(
    @SerialName("face")
    val face: String? = null,
    @SerialName("head_url")
    val head: String? = null,
    @SerialName("uid")
    val uid: Long,
    @SerialName("uname")
    val uname: String = ""
)

@Serializable
data class UserAttentions(
    @SerialName("uids")
    val users: List<Long>
)

@Serializable
data class UserLevel(
    @SerialName("current_exp")
    val currentExperience: Int? = null,
    @SerialName("current_level")
    val currentLevel: Int,
    @SerialName("current_min")
    val currentMin: Int? = null,
    @SerialName("next_exp")
    val nextExperience: Int? = null
)