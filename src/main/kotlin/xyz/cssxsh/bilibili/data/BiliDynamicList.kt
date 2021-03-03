package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.dynamic.*
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class BiliDynamicList(
    @SerialName("attentions")
    val attentions: UserAttentions? = null,
    @SerialName("cards")
    val cards: List<DynamicInfo> = emptyList(),
    @SerialName("_gt_")
    private val gt: Int,
    @SerialName("has_more")
    @Serializable(with = NumberToBooleanSerializer::class)
    val hasMore: Boolean,
    @SerialName("next_offset")
    val nextOffset: Long
)
