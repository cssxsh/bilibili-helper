package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicVideo(
    @SerialName("aid")
    val aid: Long = 0,
    @SerialName("badge")
    val badge: DynamicBadge,
    @SerialName("bvid")
    val bvid: String,
    @SerialName("cover")
    val cover: String = "",
    @SerialName("desc")
    val description: String,
    @SerialName("disable_preview")
    @Serializable(NumberToBooleanSerializer::class)
    val disablePreview: Boolean = false,
    @SerialName("duration_text")
    val duration: String,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("stat")
    val stat: Map<String, String> = emptyMap(),
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int
)