package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliDynamicDetail(
    @SerialName("attentions")
    val attentions: BiliAttentions? = null,
    @SerialName("card")
    val card: BiliCardInfo,
    @SerialName("_gt_")
    val gt: Int,
    @SerialName("result")
    val result: Int
)