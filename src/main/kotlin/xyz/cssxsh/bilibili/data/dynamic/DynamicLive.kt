package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicLive(
    @SerialName("badge")
    val badge: DynamicBadge,
    @SerialName("cover")
    val cover: String = "",
    @SerialName("desc_first")
    val description1: String = "",
    @SerialName("desc_second")
    val description2: String = "",
    @SerialName("id")
    val id: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("live_state")
    val state: Int,
    @SerialName("reserve_type")
    val reserveType: Int = 0,
    @SerialName("title")
    val title: String
) {
    val description get() = "#$description1# $description2".trim()
}