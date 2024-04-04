package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicNone(
    @SerialName("tips")
    val tips: String = ""
)