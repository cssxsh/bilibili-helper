package xyz.cssxsh.bilibili.data.live

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class LiveRecord(
    @SerialName("bvid")
    val bvid: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("global_order")
    private val globalOrder: Int,
    @SerialName("is_sticky")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isSticky: Boolean,
    @SerialName("rid")
    val roomId: String,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("title")
    val title: String,
    @SerialName("uid")
    val uid: Int,
    @SerialName("uname")
    val uname: String
)