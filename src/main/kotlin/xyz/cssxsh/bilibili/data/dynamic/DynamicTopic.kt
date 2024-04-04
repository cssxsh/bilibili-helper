package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicTopic(
    @SerialName("id")
    val id: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("name")
    val name: String
)