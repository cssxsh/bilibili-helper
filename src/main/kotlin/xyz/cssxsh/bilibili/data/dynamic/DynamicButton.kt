package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicButton(
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("jump_style")
    val jumpStyle: Style? = null,
    @SerialName("check")
    val check: Style? = null,
    @SerialName("uncheck")
    val uncheck: Style? = null,
    @SerialName("type")
    val type: Int,
    @SerialName("status")
    val status: Int = 0
) {
    @Serializable
    data class Style(
        @SerialName("icon_url")
        val iconUrl: String = "",
        @SerialName("text")
        val text: String = "",
        @SerialName("toast")
        val toast: String = "",
        @SerialName("disable")
        @Serializable(NumberToBooleanSerializer::class)
        val disable: Boolean = false
    )
}