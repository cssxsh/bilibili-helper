package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliLiveOff(
    @SerialName("recommend")
    val recommends: List<BiliLiveRecommend>,
    @SerialName("record_list")
    val records: List<BiliLiveRecord>,
    @SerialName("tips")
    val tips: String
)