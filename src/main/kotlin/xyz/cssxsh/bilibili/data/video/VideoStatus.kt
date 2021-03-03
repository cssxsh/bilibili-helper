package xyz.cssxsh.bilibili.data.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class VideoStatus(
    @SerialName("aid")
    val aid: Long,
    @SerialName("argue_msg")
    val argue: String = "",
    @SerialName("coin")
    val coin: Int,
    @SerialName("danmaku")
    val danmaku: Int,
    @SerialName("dislike")
    @Serializable(with = NumberToBooleanSerializer::class)
    val dislike: Boolean,
    @SerialName("evaluation")
    val evaluation: String = "",
    @SerialName("favorite")
    val favorite: Int,
    @SerialName("his_rank")
    @Serializable(with = NumberToBooleanSerializer::class)
    val hisRank: Boolean,
    @SerialName("like")
    val like: Int,
    @SerialName("now_rank")
    val nowRank: Int,
    @SerialName("reply")
    val reply: Int,
    @SerialName("share")
    val share: Int,
    @SerialName("view")
    val view: Int
)