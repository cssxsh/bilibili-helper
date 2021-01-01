package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliCardInfo(
    @SerialName("card")
    val card: String,
    @SerialName("desc")
    val desc: Desc,
    @SerialName("display")
    val display: JsonElement,
    @SerialName("extend_json")
    val extendJson: String,
    @SerialName("extra")
    val extra: JsonElement? = null,
    @SerialName("activity_infos")
    val activityInfos: JsonElement? = null,
    @SerialName("extension")
    val extension: JsonElement? = null
) {

    @Serializable
    data class Desc(
        @SerialName("acl")
        val acl: Int? = null,
        @SerialName("comment")
        val comment: Int? = null,
        @SerialName("dynamic_id")
        val dynamicId: Long,
        @SerialName("dynamic_id_str")
        val dynamicIdStr: String? = null,
        @SerialName("inner_id")
        val innerId: Long? = null,
        @SerialName("is_liked")
        val isLiked: Int? = null,
        @SerialName("like")
        val like: Int,
        @SerialName("orig_dy_id")
        val origDyId: Long? = null,
        @SerialName("orig_dy_id_str")
        val origDyIdStr: String? = null,
        @SerialName("orig_type")
        val origType: Int? = null,
        @SerialName("origin")
        val origin: JsonElement? = null,
        @SerialName("pre_dy_id")
        val preDyId: Long? = null,
        @SerialName("pre_dy_id_str")
        val preDyIdStr: String? = null,
        @SerialName("r_type")
        val rType: Int,
        @SerialName("repost")
        val repost: Int,
        @SerialName("rid")
        val rid: Long? = null,
        @SerialName("rid_str")
        val ridStr: String? = null,
        @SerialName("status")
        val status: Int,
        @SerialName("stype")
        val sType: Int? = null,
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
        val view: Int,
        @SerialName("bvid")
        val bvId: String? = null,
        @SerialName("previous")
        val previous: JsonElement? = null
    )
}