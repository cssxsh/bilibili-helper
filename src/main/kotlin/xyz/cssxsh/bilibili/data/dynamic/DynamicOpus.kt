package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicOpus(
    @SerialName("fold_action")
    val foldAction: List<String> = emptyList(),
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("pics")
    val pictures: List<DynamicPictureItem>,
    @SerialName("summary")
    val summary: RichText,
    @SerialName("title")
    val title: String? = null
)