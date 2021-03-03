package xyz.cssxsh.bilibili.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLevel(
    @SerialName("current_exp")
    val currentExperience: Int? = null,
    @SerialName("current_level")
    val currentLevel: Int,
    @SerialName("current_min")
    val currentMin: Int? = null,
    @SerialName("next_exp")
    val nextExperience: Int? = null
)