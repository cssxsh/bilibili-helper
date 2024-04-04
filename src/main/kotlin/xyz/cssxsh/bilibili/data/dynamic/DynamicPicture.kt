package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicPicture(
    @SerialName("id")
    val id: Long,
    @SerialName("items")
    val items: List<DynamicPictureItem>
)