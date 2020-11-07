package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliRoomInfo(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val roomData: RoomData,
    @SerialName("message")
    val message: String,
    @SerialName("msg")
    val msg: String
) {
    @Serializable
    data class RoomData(
        @SerialName("encrypted")
        val encrypted: Boolean,
        @SerialName("hidden_till")
        val hiddenTill: Int,
        @SerialName("is_hidden")
        val isHidden: Boolean,
        @SerialName("is_locked")
        val isLocked: Boolean,
        @SerialName("is_portrait")
        val isPortrait: Boolean,
        @SerialName("is_sp")
        val isSp: Int,
        @SerialName("live_status")
        val liveStatus: Int,
        @SerialName("live_time")
        val liveTime: Int,
        @SerialName("lock_till")
        val lockTill: Int,
        @SerialName("need_p2p")
        val needP2p: Int,
        @SerialName("pwd_verified")
        val pwdVerified: Boolean,
        @SerialName("room_id")
        val roomId: Int,
        @SerialName("room_shield")
        val roomShield: Int,
        @SerialName("short_id")
        val shortId: Int,
        @SerialName("special_type")
        val specialType: Int,
        @SerialName("uid")
        val uid: Int
    )
}