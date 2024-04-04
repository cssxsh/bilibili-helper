package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.data.dynamic.*

@Serializable
data class BiliDynamicDetail(
    @SerialName("item")
    val item: DynamicDetail
)

@Serializable
data class BiliDynamicSpace(
    @SerialName("items")
    val items: List<DynamicDetail> = emptyList(),
    @SerialName("has_more")
    val more: Boolean = false,
    @SerialName("offset")
    val offset: Long? = null,
    @SerialName("update_baseline")
    val updateBaseLine: String = "",
    @SerialName("update_num")
    val updateNumber: Int = 0
)