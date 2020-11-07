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
        val liveRoom: LiveRoom,
        @SerialName("mid")
        val mid: Int,
        @SerialName("moral")
        val moral: Int,
        @SerialName("name")
        val name: String,
        @SerialName("nameplate")
        val nameplate: Nameplate,
        @SerialName("official")
        val official: Official,
        @SerialName("pendant")
        val pendant: Pendant,
        @SerialName("rank")
        val rank: Int,
        @SerialName("sex")
        val sex: String,
        @SerialName("sign")
        val sign: String,
        @SerialName("silence")
        val silence: Int,
        @SerialName("sys_notice")
        val sysNotice: JsonElement,
        @SerialName("theme")
        val theme: JsonElement,
        @SerialName("top_photo")
        val topPhoto: String,
        @SerialName("vip")
        val vip: Vip
    ) {
        @Serializable
        data class LiveRoom(
            @SerialName("broadcast_type")
            val broadcastType: Int,
            @SerialName("cover")
            val cover: String,
            @SerialName("liveStatus")
            val liveStatus: Int,
            @SerialName("online")
            val online: Int,
            @SerialName("roomStatus")
            val roomStatus: Int,
            @SerialName("roomid")
            val roomId: Int,
            @SerialName("roundStatus")
            val roundStatus: Int,
            @SerialName("title")
            val title: String,
            @SerialName("url")
            val url: String
        )

        @Serializable
        data class Nameplate(
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
            val nid: Int
        )

        @Serializable
        data class Official(
            @SerialName("desc")
            val desc: String,
            @SerialName("role")
            val role: Int,
            @SerialName("title")
            val title: String,
            @SerialName("type")
            val type: Int
        )

        @Serializable
        data class Pendant(
            @SerialName("expire")
            val expire: Int,
            @SerialName("image")
            val image: String,
            @SerialName("image_enhance")
            val imageEnhance: String,
            @SerialName("name")
            val name: String,
            @SerialName("pid")
            val pid: Int
        )

        @Serializable
        data class Vip(
            @SerialName("avatar_subscript")
            val avatarSubscript: Int,
            @SerialName("label")
            val label: Label,
            @SerialName("nickname_color")
            val nicknameColor: String,
            @SerialName("status")
            val status: Int,
            @SerialName("theme_type")
            val themeType: Int,
            @SerialName("type")
            val type: Int
        ) {
            @Serializable
            data class Label(
                @SerialName("label_theme")
                val labelTheme: String,
                @SerialName("path")
                val path: String,
                @SerialName("text")
                val text: String
            )
        }
    }
}