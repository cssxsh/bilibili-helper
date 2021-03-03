package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.live.*

@Serializable
data class BiliLiveOff(
    @SerialName("recommend")
    val recommends: List<LiveRecommend>,
    @SerialName("record_list")
    val records: List<LiveRecord>,
    @SerialName("tips")
    val tips: String
)