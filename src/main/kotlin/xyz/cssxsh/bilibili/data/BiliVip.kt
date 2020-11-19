package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliVip(
    @SerialName("accessStatus")
    val accessStatus: Int? = null,
    @SerialName("dueRemark")
    val dueRemark: String? = null,
    @SerialName("label")
    val label: Label,
    @SerialName("themeType")
    val themeType: Int? = null,
    @SerialName("vipDueDate")
    val vipDueDate: Long,
    @SerialName("vipStatus")
    val vipStatus: Int,
    @SerialName("vipStatusWarn")
    val vipStatusWarn: String,
    @SerialName("vipType")
    val vipType: Int
) {
    @Serializable
    data class Label(
        @SerialName("path")
        val path: String
    )
}