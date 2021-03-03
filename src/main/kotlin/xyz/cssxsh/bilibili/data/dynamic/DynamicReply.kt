package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class DynamicReply(
    @SerialName("item")
    val item: Item,
    @SerialName("origin")
    val origin: JsonObject,
    @SerialName("origin_extend_json")
    private val originExtendJson: JsonObject,
    @SerialName("origin_user")
    val originUser: UserProfile,
    @SerialName("user")
    val user: UserSimple,
    @SerialName("activity_infos")
    private val activityInfos: JsonObject? = null,
    @SerialName("extension")
    private val extension: JsonObject? = null,
    @SerialName("origin_extension")
    private val originExtension: JsonObject? = null
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
        val originDynamicId: Long,
        @SerialName("orig_type")
        val originType: DynamicType,
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