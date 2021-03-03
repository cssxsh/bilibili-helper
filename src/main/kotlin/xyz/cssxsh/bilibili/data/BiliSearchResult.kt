package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class BiliSearchResult(
    @SerialName("episodic_button")
    private val episodicButton: JsonObject? = null,
    @SerialName("list")
    val list: InfoList,
    @SerialName("page")
    val page: Page
) {

    @Serializable
    data class TypeInfo(
        @SerialName("count")
        val count: Int,
        @SerialName("name")
        val name: String,
        @SerialName("tid")
        val tid: Long
    )

    @Serializable
    data class VideoInfo(
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
        val uid: Long,
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

    @Serializable
    data class InfoList(
        @SerialName("tlist")
        val typeList: Map<Int, TypeInfo>? = null,
        @SerialName("vlist")
        val videoList: List<VideoInfo>
    )

    @Serializable
    data class Page(
        @SerialName("count")
        val count: Int,
        @SerialName("pn")
        val num: Int,
        @SerialName("ps")
        val size: Int
    )
}