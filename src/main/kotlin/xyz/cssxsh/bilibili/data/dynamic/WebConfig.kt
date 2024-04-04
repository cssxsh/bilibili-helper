package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class WebConfig(
    @SerialName("config_type")
    val type: Int = 0,
    @SerialName("general_config")
    val general: General? = null
) {
    @Serializable
    data class General(
        @SerialName("web_css_style")
        val css: Map<String, String> = emptyMap()
    )
}