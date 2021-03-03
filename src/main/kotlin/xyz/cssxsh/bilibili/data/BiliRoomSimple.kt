package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class BiliRoomSimple(
    @SerialName("broadcast_type")
    private val broadcastType: Int,
    @SerialName("cover")
    val cover: String,
    @SerialName("link")
    val link: String? = null,
    @SerialName("liveStatus")
    @Serializable(with = NumberToBooleanSerializer::class)
    val liveStatus: Boolean,
    @SerialName("online")
    val online: Int,
    @SerialName("online_hidden")
    @Serializable(with = NumberToBooleanSerializer::class)
    val onlineHidden: Boolean= false,
    @SerialName("roomStatus")
    @Serializable(with = NumberToBooleanSerializer::class)
    val roomStatus: Boolean,
    @SerialName("roomid")
    val roomId: Long,
    @SerialName("roundStatus")
    @Serializable(with = NumberToBooleanSerializer::class)
    val roundStatus: Boolean,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String
)