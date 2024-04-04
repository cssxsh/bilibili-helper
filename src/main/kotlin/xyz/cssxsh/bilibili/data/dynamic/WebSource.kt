package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class WebSource(
    @SerialName("local")
    val local: Int? = null,
    @SerialName("placeholder")
    val placeholder: Int? = null,
    @SerialName("remote")
    val remote: Remote? = null,
    @SerialName("src_type")
    val type: Int = 0
) {
    @Serializable
    data class Remote(
        @SerialName("bfs_style")
        val bfsStyle: String = "",
        @SerialName("url")
        val url: String = ""
    )
}