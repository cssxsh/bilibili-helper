package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliRoomInfo(
    @SerialName("encrypted")
    val encrypted: Boolean,
    @SerialName("hidden_till")
    @Serializable(with = NumberToBooleanSerializer::class)
    val hiddenTill: Boolean,
    @SerialName("is_hidden")
    val isHidden: Boolean,
    @SerialName("is_locked")
    val isLocked: Boolean,
    @SerialName("is_portrait")
    val isPortrait: Boolean,
    @SerialName("is_sp")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isSmartPhone: Boolean,
    @SerialName("live_status")
    val liveType: Int,
    @SerialName("live_time")
    val liveTime: Long,
    @SerialName("lock_till")
    @Serializable(with = NumberToBooleanSerializer::class)
    val lockTill: Boolean,
    @SerialName("need_p2p")
    @Serializable(with = NumberToBooleanSerializer::class)
    private val needP2P: Boolean,
    @SerialName("pwd_verified")
    val passwordVerified: Boolean,
    @SerialName("room_id")
    val roomId: Long,
    @SerialName("room_shield")
    @Serializable(with = NumberToBooleanSerializer::class)
    private val roomShield: Boolean,
    @SerialName("short_id")
    val shortId: Int,
    @SerialName("special_type")
    private val specialType: Int, // XXX
    @SerialName("uid")
    val uid: Long
)