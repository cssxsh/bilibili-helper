package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliDynamicInfo(
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
