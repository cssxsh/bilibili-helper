package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicMediaList(
    @SerialName("badge")
    val badge: DynamicBadge,
    @SerialName("cover")
    val cover: String = "",
    @SerialName("cover_type")
    val coverType: Int = 0,
    @SerialName("id")
    val id: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("sub_title")
    val subtitle: String = "",
    @SerialName("title")
    val title: String
)