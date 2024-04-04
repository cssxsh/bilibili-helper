package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicCourses(
    @SerialName("aid")
    val aid: Long = 0,
    @SerialName("badge")
    val badge: DynamicBadge,
    @SerialName("id")
    val id: Long,
    @SerialName("cover")
    val cover: String = "",
    @SerialName("desc")
    val description: String,
    @SerialName("enable_preview")
    @Serializable(NumberToBooleanSerializer::class)
    val enablePreview: Boolean = true,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("title")
    val title: String,
    @SerialName("sub_title")
    val subtitle: String
)