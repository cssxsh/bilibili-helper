package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicSketch(
    @SerialName("badge")
    val badge: DynamicBadge,
    @SerialName("biz_type")
    val bizType: Int,
    @SerialName("cover")
    val cover: String = "",
    @SerialName("desc")
    val description: String,
    @SerialName("id")
    val id: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("label")
    val label: String,
    @SerialName("sketch_id")
    val sketchId: Long,
    @SerialName("style")
    val style: Int = 0,
    @SerialName("title")
    val title: String
)