package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliLevelInfo(
    @SerialName("current_exp")
    val currentExp: Int,
    @SerialName("current_level")
    val currentLevel: Int,
    @SerialName("current_min")
    val currentMin: Int,
    @SerialName("next_exp")
    val nextExp: String
)