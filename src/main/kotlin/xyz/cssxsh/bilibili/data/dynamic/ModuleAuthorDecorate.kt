package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleAuthorDecorate(
    @SerialName("card_url")
    val cardUrl: String = "",
    @SerialName("fan")
    val fan: Fan,
    @SerialName("id")
    val id: Long = 0,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: Int
) {
    @Serializable
    data class Fan(
        @SerialName("color")
        val color: String,
        @SerialName("is_fan")
        val isFan: Boolean,
        @SerialName("num_str")
        val id: String,
        @SerialName("number")
        val number: Int
    )
}