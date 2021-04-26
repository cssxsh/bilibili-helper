package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.user.UserSimple

@Serializable
data class DynamicSketch(
    @SerialName("rid")
    val rid: Long,
    @SerialName("sketch")
    val sketch: Sketch,
    @SerialName("user")
    val user: UserSimple,
    @SerialName("vest")
    val vest: Vest
) {

    @Serializable
    data class Sketch(
        @SerialName("biz_id")
        val bizId: Int,
        @SerialName("biz_type")
        val bizType: Int,
        @SerialName("cover_url")
        val coverUrl: String,
        @SerialName("desc_text")
        val descText: String,
        @SerialName("sketch_id")
        val sketchId: Long,
        @SerialName("target_url")
        val targetUrl: String,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Vest(
        @SerialName("content")
        val content: String,
        @SerialName("ctrl")
        private val ctrl: String? = null,
        @SerialName("uid")
        val uid: Int
    )
}