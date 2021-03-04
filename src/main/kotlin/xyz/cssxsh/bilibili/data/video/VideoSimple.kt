package xyz.cssxsh.bilibili.data.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@kotlinx.serialization.Serializable
data class VideoSimple(
    @SerialName("aid")
    val aid: Long,
    @SerialName("author")
    val author: String,
    @SerialName("bvid")
    val bvid: String,
    @SerialName("comment")
    val comment: Int,
    @SerialName("copyright")
    val copyright: String,
    @SerialName("created")
    val created: Long,
    @SerialName("description")
    val description: String,
    @SerialName("hide_click")
    val hideClick: Boolean,
    @SerialName("is_pay")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isPay: Boolean,
    @SerialName("is_union_video")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isUnionVideo: Boolean,
    @SerialName("is_steins_gate")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isSteinsGate: Boolean,
    @SerialName("length")
    val length: String,
    @SerialName("mid")
    val mid: Long,
    @SerialName("pic")
    val picture: String,
    @SerialName("play")
    val play: Int,
    @SerialName("review")
    val review: Int,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    val title: String,
    @SerialName("typeid")
    val typeId: Int,
    @SerialName("video_review")
    val videoReview: Int
)