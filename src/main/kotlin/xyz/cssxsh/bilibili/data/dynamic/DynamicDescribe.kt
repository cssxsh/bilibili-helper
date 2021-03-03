package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class DynamicDescribe(
    @SerialName("acl")
    private val acl: Int? = null,
    @SerialName("bvid")
    val bvid: String? = null,
    @SerialName("comment")
    val comment: Int = 0,
    @SerialName("dynamic_id")
    val dynamicId: Long,
    @SerialName("dynamic_id_str")
    private val dynamicIdString: String? = null,
    @SerialName("inner_id")
    private val innerId: Long? = null,
    @SerialName("is_liked")
    @Serializable(with = NumberToBooleanSerializer::class)
    val isLiked: Boolean = false,
    @SerialName("like")
    val like: Int = 0,
    @SerialName("origin")
    val origin: DynamicDescribe? = null,
    @SerialName("orig_dy_id")
    val originDynamicId: Long? = null,
    @SerialName("orig_dy_id_str")
    private val originDynamicIdString: String? = null,
    @SerialName("orig_type")
    val originType: DynamicType? = null,
    @SerialName("previous")
    val previous: DynamicDescribe? = null,
    @SerialName("pre_dy_id")
    val previousDynamicId: Long? = null,
    @SerialName("pre_dy_id_str")
    private val previousDynamicIdString: String? = null,
    @SerialName("repost")
    val repost: Int = 0,
    @SerialName("rid")
    private val rid: Long? = null,
    @SerialName("rid_str")
    private val ridString: String? = null,
    @SerialName("r_type")
    private val rType: Int? = null,
    @SerialName("status")
    val status: Int, // XXX
    @SerialName("stype")
    private val statusType: Int? = null,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("type")
    val type: DynamicType,
    @SerialName("uid")
    val uid: Long,
    @SerialName("uid_type")
    private val uidType: Int? = null,
    @SerialName("user_profile")
    val profile: UserProfile? = null,
    @SerialName("view")
    val view: Int
)