package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliReplyCard(
    @SerialName("item")
    val item: Item,
    @SerialName("origin")
    val origin: String,
    @SerialName("origin_extend_json")
    val originExtendJson: String,
    @SerialName("origin_user")
    val originUser: BiliUserProfile,
    @SerialName("user")
    val user: BiliCardUser,
    @SerialName("activity_infos")
    val activityInfos: JsonElement? = null,
    @SerialName("extension")
    val extension: JsonElement? = null,
    @SerialName("origin_extension")
    val originExtension: JsonElement? = null
) {

    companion object {
        const val TYPE = 1
    }

    @Serializable
    data class Item(
        @SerialName("content")
        val content: String,
        @SerialName("ctrl")
        val ctrl: String = "",
        @SerialName("orig_dy_id")
        val origDyId: Long,
        @SerialName("orig_type")
        val origType: Int,
        @SerialName("pre_dy_id")
        val preDyId: Long,
        @SerialName("reply")
        val reply: Int,
        @SerialName("rp_id")
        val replyId: Long,
        @SerialName("timestamp")
        val timestamp: Int,
        @SerialName("uid")
        val uid: Long,
        @SerialName("at_uids")
        val atUIDs: List<Long> = emptyList()
    )
}