package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class RichText(
    @SerialName("rich_text_nodes")
    val nodes: List<RichTextNode>,
    @SerialName("text")
    val text: String
) {
    companion object {
        fun simple(text: String): RichText = RichText(text = text, nodes = emptyList())
    }
}