package xyz.cssxsh.bilibili.data.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class ArticleStatus(
    @SerialName("coin")
    val coin: Int,
    @SerialName("dislike")
    @Serializable(with = NumberToBooleanSerializer::class)
    val dislike: Boolean,
    @SerialName("dynamic")
    val dynamic: Int,
    @SerialName("favorite")
    val favorite: Int,
    @SerialName("like")
    val like: Int,
    @SerialName("reply")
    val reply: Int,
    @SerialName("share")
    val share: Int,
    @SerialName("view")
    val view: Int
)