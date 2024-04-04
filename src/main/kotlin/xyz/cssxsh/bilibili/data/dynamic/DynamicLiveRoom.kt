package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicLiveRoom(
    @SerialName("content")
    @Serializable(RoomInfo.AsStringSerializer::class)
    val info: RoomInfo,
    @SerialName("reserve_type")
    val reserveType: Int
)