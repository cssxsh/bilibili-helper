package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleDynamic(
    @SerialName("additional")
    val additional: DynamicAdditional? = null,
    @SerialName("desc")
    val describe: RichText? = null,
    @SerialName("major")
    val major: DynamicMajor? = null,
    @SerialName("topic")
    val topic: DynamicTopic? = null,
)