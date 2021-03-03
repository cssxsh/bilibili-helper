package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class DynamicInfo(
    @SerialName("activity_infos")
    private val activityInfos: JsonObject? = null,
    @SerialName("card")
    val card: JsonElement,
    @SerialName("desc")
    val describe: DynamicDescribe,
    @SerialName("display")
    private val display: JsonObject,
    @SerialName("extend_json")
    private val extendJson: JsonElement,
    @SerialName("extension")
    private val extension: JsonObject? = null,
    @SerialName("extra")
    private val extra: JsonObject? = null
)