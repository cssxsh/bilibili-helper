package xyz.cssxsh.bilibili.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliDynamicInfo(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val dynamicData: DynamicData,
    @SerialName("message")
    val message: String,
    @SerialName("msg")
    val msg: String
) {

    @Serializable
    data class DynamicData(
        @SerialName("attentions")
        val attentions: BiliAttentions? = null,
        @SerialName("cards")
        val cards: List<BiliCardInfo>,
        @SerialName("_gt_")
        val gt: Int,
        @SerialName("has_more")
        val hasMore: Int,
        @SerialName("next_offset")
        val nextOffset: Long
    )
}