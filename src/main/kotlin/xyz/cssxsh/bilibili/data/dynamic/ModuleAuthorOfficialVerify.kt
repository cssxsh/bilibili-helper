package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleAuthorOfficialVerify(
    @SerialName("desc")
    val description: String,
    @SerialName("type")
    val type: Int
)