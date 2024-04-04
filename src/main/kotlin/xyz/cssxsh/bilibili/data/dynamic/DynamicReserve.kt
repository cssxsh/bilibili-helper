package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicReserve(
    @SerialName("button")
    val button: DynamicButton,
    @SerialName("desc1")
    val description1: Description = Description.Empty,
    @SerialName("desc2")
    val description2: Description = Description.Empty,
    @SerialName("desc3")
    val description3: Description = Description.Empty,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("reserve_total")
    val total: Int,
    @SerialName("rid")
    val rid: Long,
    @SerialName("state")
    val state: Int = 0,
    @SerialName("stype")
    val type: Int = 0,
    @SerialName("title")
    val title: String,
    @SerialName("up_mid")
    val mid: Long
) {
    val description get() = "${description1.text} ${description2.text} ${description3.text}".trim()

    @Serializable
    data class Description(
        @SerialName("jump_url")
        val jumpUrl: String = "",
        @SerialName("style")
        val style: Int = 0,
        @SerialName("text")
        val text: String = "",
        @SerialName("visible")
        val visible: Boolean = true
    ) {
        companion object {
            val Empty = Description(
                jumpUrl = "",
                style = 0,
                text = "",
                visible = false
            )
        }
    }
}