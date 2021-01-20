package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliCardInfo(
    @SerialName("activity_infos")
    val activityInfos: JsonElement? = null,
    @SerialName("card")
    val card: String,
    @SerialName("desc")
    val describe: Describe,
    @SerialName("display")
    val display: JsonElement,
    @SerialName("extend_json")
    val extendJson: String,
    @SerialName("extension")
    val extension: JsonElement? = null,
    @SerialName("extra")
    val extra: JsonElement? = null
) {

    @Serializable
    data class Describe(
        @SerialName("acl")
        val acl: Int? = null,
        @SerialName("bvid")
        val bvId: String? = null,
        @SerialName("comment")
        val comment: Int? = null,
        @SerialName("dynamic_id")
        val dynamicId: Long,
        @SerialName("dynamic_id_str")
        val dynamicIdString: String? = null,
        @SerialName("inner_id")
        val innerId: Long? = null,
        @SerialName("is_liked")
        val isLiked: Int? = null,
        @SerialName("like")
        val like: Int,
        @SerialName("origin")
        val origin: JsonElement? = null,
        @SerialName("orig_dy_id")
        val originDynamicId: Long? = null,
        @SerialName("orig_dy_id_str")
        val originDynamicIdString: String? = null,
        @SerialName("orig_type")
        val originType: Int? = null,
        @SerialName("previous")
        val previous: JsonElement? = null,
        @SerialName("pre_dy_id")
        val previousDynamicId: Long? = null,
        @SerialName("pre_dy_id_str")
        val previousDynamicIdString: String? = null,
        @SerialName("repost")
        val repost: Int,
        @SerialName("r_type")
        val repostType: Int? = null,
        @SerialName("rid")
        val repostId: Long? = null,
        @SerialName("rid_str")
        val repostIdString: String? = null,
        @SerialName("status")
        val status: Int,
        @SerialName("stype")
        val statusType: Int? = null,
        @SerialName("timestamp")
        val timestamp: Long,
        @SerialName("type")
        val type: Int,
        @SerialName("uid")
        val uid: Long,
        @SerialName("uid_type")
        val uidType: Int? = null,
        @SerialName("user_profile")
        val userProfile: BiliUserProfile,
        @SerialName("view")
        val view: Int
    )
}