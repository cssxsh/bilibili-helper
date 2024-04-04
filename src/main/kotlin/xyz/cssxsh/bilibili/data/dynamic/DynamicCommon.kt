package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicCommon(
    @SerialName("button")
    val button: DynamicButton,
    @SerialName("cover")
    val cover: String = "",
    @SerialName("desc1")
    val description1: String = "",
    @SerialName("desc2")
    val description2: String = "",
    @SerialName("head_text")
    val head: String = "",
    @SerialName("id_str")
    val id: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("style")
    val style: Int = 0,
    @SerialName("sub_type")
    val subType: String = "",
    @SerialName("title")
    val title: String
) {
    val description get() = "#$description1# $description2".trim()
}