package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliVideoState(
    @SerialName("aid")
    val aid: Long,
    @SerialName("coin")
    val coin: Int,
    @SerialName("danmaku")
    val danmaku: Int,
    @SerialName("dislike")
    val dislike: Int,
    @SerialName("favorite")
    val favorite: Int,
    @SerialName("his_rank")
    val hisRank: Int,
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