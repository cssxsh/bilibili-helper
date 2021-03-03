package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class DynamicText(
    @SerialName("item")
    val item: Item,
    @SerialName("user")
    val user: UserSimple,
    @SerialName("activity_infos")
    private val activityInfos: JsonObject? = null,
    @SerialName("extension")
    private val extension: JsonObject? = null
) {
    @Serializable
    data class Item(
        @SerialName("at_uids")
        val atUsers: List<Long> = emptyList(),
        @SerialName("content")
        val content: String,
        @SerialName("ctrl")
        private val ctrl: String? = null,
        @SerialName("orig_dy_id")
        private val originDynamicId: Long,
        @SerialName("pre_dy_id")
        private val previousDynamicId: Long,
        @SerialName("reply")
        val reply: Int,
        @SerialName("rp_id")
        private val replyId: Long,
        @SerialName("timestamp")
        val timestamp: Int,
        @SerialName("uid")
        val uid: Long
    )
}