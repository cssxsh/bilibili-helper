package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class DynamicInfo(
    @SerialName("activity_infos")
    private val activityInfos: JsonObject? = null,
    @SerialName("card")
    val card: String,
    @SerialName("desc")
    val describe: DynamicDescribe,
    @SerialName("need_refresh")
    @Serializable(with = NumberToBooleanSerializer::class)
    val needRefresh: Boolean = false,
    @SerialName("display")
    private val display: JsonObject,
    @SerialName("extend_json")
    private val extendJson: String,
    @SerialName("extension")
    private val extension: JsonObject? = null,
    @SerialName("extra")
    private val extra: JsonObject? = null
)