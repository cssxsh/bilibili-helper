package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicSeason(
    @SerialName("cover")
    val cover: String = "",
    @SerialName("desc_first")
    val description1: String = "",
    @SerialName("desc_second")
    val description2: String = "",
    @SerialName("duration")
    val duration: String = "",
    @SerialName("head_text")
    val head: String = "",
    @SerialName("id_str")
    val id: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("multi_line")
    val multiline: Boolean = false,
    @SerialName("title")
    val title: String = ""
) {
    val description get() = "$description1 $description2".trim()
}