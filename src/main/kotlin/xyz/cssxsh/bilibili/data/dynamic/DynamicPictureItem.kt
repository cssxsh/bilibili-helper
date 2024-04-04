package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class DynamicPictureItem(
    @SerialName("height")
    val height: Int,
    @SerialName("size")
    val size: Double,
    @JsonNames("src", "url")
    val source: String,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
    @SerialName("width")
    val width: Int
)