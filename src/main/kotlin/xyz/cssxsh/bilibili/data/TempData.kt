package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TempData(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: JsonElement? = null,
    @SerialName("result")
    val result: JsonElement? = null,
    @SerialName("message")
    val message: String,
    @SerialName("ttl")
    val ttl: Int? = null,
    @SerialName("msg")
    val msg: String? = null
)

