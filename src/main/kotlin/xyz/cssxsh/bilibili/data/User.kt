package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*

sealed interface UserInfo : Entry {
    val name: String
    val level: Int
    val sign: String
    val live: String
    val face: String
    val mid: Long
}

@Serializable
data class BiliUserInfo(
    @SerialName("birthday")
    val birthday: String,
    @SerialName("coins")
    val coins: Long,
    @SerialName("face")
    override val face: String,
    @SerialName("fans_badge")
    val fansBadge: Boolean,
    @SerialName("is_followed")
    val isFollowed: Boolean,
    @SerialName("jointime")
    val joined: Long,
    @SerialName("level")
    override val level: Int,
    @SerialName("live_room")
    val liveRoom: BiliRoomSimple,
    @SerialName("mid")
    override val mid: Long,
    @SerialName("moral")
    val moral: Int,
    @SerialName("name")
    override val name: String,
    @SerialName("nameplate")
    val nameplate: UserNameplate,
    @SerialName("official")
    val official: UserOfficial,
    @SerialName("rank")
    val rank: Int,
    @SerialName("sex")
    val sex: String,
    @SerialName("sign")
    override val sign: String,
    @SerialName("top_photo")
    val topPhoto: String,
) : UserInfo {
    override val live: String get() = liveRoom.link
}

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
data class UserProfile(
    @SerialName("info")
    val user: UserSimple,
    @SerialName("rank")
    val rank: String? = null,
    @SerialName("sign")
    val sign: String? = null
)

@Serializable
data class UserOfficial(
    @SerialName("desc")
    val description: String,
    @SerialName("role")
    val role: Int = 0,
    @SerialName("title")
    val title: String = "",
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