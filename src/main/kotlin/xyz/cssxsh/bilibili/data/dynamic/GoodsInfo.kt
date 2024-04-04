package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class GoodsInfo(
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("type")
    val type: Int = 0
)