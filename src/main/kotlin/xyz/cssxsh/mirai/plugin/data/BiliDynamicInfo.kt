package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
data class BiliDynamicInfo(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val dynamicData: DynamicData,
    @SerialName("message")
    val message: String,
    @SerialName("msg")
    val msg: String
) {
    @Serializable
    data class DynamicData(
        @SerialName("attentions")
        val attentions: Attentions,
        @SerialName("cards")
        val cards: List<Card>,
        @SerialName("_gt_")
        val gt: Int,
        @SerialName("has_more")
        val hasMore: Int,
        @SerialName("next_offset")
        val nextOffset: Long
    ) {
        @Serializable
        data class Attentions(
            @SerialName("uids")
            val uids: List<Int>
        )

        @Serializable
        data class Card(
            @SerialName("card")
            @Serializable(BiliCardJsonSerializer::class)
            val card: BiliCardInfo,
            @SerialName("desc")
            val desc: Desc,
            @SerialName("display")
            val display: Display,
            @SerialName("extend_json")
            @Serializable(BiliExtendJsonSerializer::class)
            val extendJson: BiliExtendJson,
            @SerialName("extra")
            val extra: Extra
        ) {
            companion object {
                object BiliCardJsonSerializer : KSerializer<BiliCardInfo> {
                    override fun deserialize(decoder: Decoder): BiliCardInfo =
                        Json.decodeFromString(BiliCardInfo.serializer(), decoder.decodeString())


                    override val descriptor: SerialDescriptor
                        get() = PrimitiveSerialDescriptor("BiliCardJsonSerializer", PrimitiveKind.STRING)

                    override fun serialize(encoder: Encoder, value: BiliCardInfo) {
                        encoder.encodeString(Json.encodeToString(BiliCardInfo.serializer(), value))
                    }
                }

                object BiliExtendJsonSerializer : KSerializer<BiliExtendJson> {
                    override fun deserialize(decoder: Decoder): BiliExtendJson =
                        Json.decodeFromString(BiliExtendJson.serializer(), decoder.decodeString())


                    override val descriptor: SerialDescriptor
                        get() = PrimitiveSerialDescriptor("BiliExtendJsonSerializer", PrimitiveKind.STRING)

                    override fun serialize(encoder: Encoder, value: BiliExtendJson) {
                        encoder.encodeString(Json.encodeToString(BiliExtendJson.serializer(), value))
                    }
                }
            }

            @Serializable
            data class Desc(
                @SerialName("acl")
                val acl: Int,
                @SerialName("comment")
                val comment: Int,
                @SerialName("dynamic_id")
                val dynamicId: Long,
                @SerialName("dynamic_id_str")
                val dynamicIdStr: String,
                @SerialName("inner_id")
                val innerId: Int,
                @SerialName("is_liked")
                val isLiked: Int,
                @SerialName("like")
                val like: Int,
                @SerialName("orig_dy_id")
                val origDyId: Int,
                @SerialName("orig_dy_id_str")
                val origDyIdStr: String,
                @SerialName("orig_type")
                val origType: Int,
                @SerialName("origin")
                val origin: Origin,
                @SerialName("pre_dy_id")
                val preDyId: Int,
                @SerialName("pre_dy_id_str")
                val preDyIdStr: String,
                @SerialName("r_type")
                val rType: Int,
                @SerialName("repost")
                val repost: Int,
                @SerialName("rid")
                val rid: Int,
                @SerialName("rid_str")
                val ridStr: String,
                @SerialName("status")
                val status: Int,
                @SerialName("stype")
                val stype: Int,
                @SerialName("timestamp")
                val timestamp: Int,
                @SerialName("type")
                val type: Int,
                @SerialName("uid")
                val uid: Int,
                @SerialName("uid_type")
                val uidType: Int,
                @SerialName("user_profile")
                val userProfile: UserProfile,
                @SerialName("view")
                val view: Int
            ) {
                @Serializable
                data class Origin(
                    @SerialName("acl")
                    val acl: Int,
                    @SerialName("dynamic_id")
                    val dynamicId: Long,
                    @SerialName("dynamic_id_str")
                    val dynamicIdStr: String,
                    @SerialName("inner_id")
                    val innerId: Int,
                    @SerialName("like")
                    val like: Int,
                    @SerialName("orig_dy_id")
                    val origDyId: Int,
                    @SerialName("orig_dy_id_str")
                    val origDyIdStr: String,
                    @SerialName("pre_dy_id")
                    val preDyId: Int,
                    @SerialName("pre_dy_id_str")
                    val preDyIdStr: String,
                    @SerialName("r_type")
                    val rType: Int,
                    @SerialName("repost")
                    val repost: Int,
                    @SerialName("rid")
                    val rid: Long,
                    @SerialName("rid_str")
                    val ridStr: String,
                    @SerialName("status")
                    val status: Int,
                    @SerialName("stype")
                    val stype: Int,
                    @SerialName("timestamp")
                    val timestamp: Int,
                    @SerialName("type")
                    val type: Int,
                    @SerialName("uid")
                    val uid: Int,
                    @SerialName("uid_type")
                    val uidType: Int,
                    @SerialName("view")
                    val view: Int
                )

                @Serializable
                data class UserProfile(
                    @SerialName("card")
                    val card: Card,
                    @SerialName("info")
                    val info: Info,
                    @SerialName("level_info")
                    val levelInfo: LevelInfo,
                    @SerialName("pendant")
                    val pendant: Pendant,
                    @SerialName("rank")
                    val rank: String,
                    @SerialName("sign")
                    val sign: String,
                    @SerialName("vip")
                    val vip: Vip
                ) {
                    @Serializable
                    data class Card(
                        @SerialName("official_verify")
                        val officialVerify: OfficialVerify
                    ) {
                        @Serializable
                        data class OfficialVerify(
                            @SerialName("desc")
                            val desc: String,
                            @SerialName("type")
                            val type: Int
                        )
                    }

                    @Serializable
                    data class Info(
                        @SerialName("face")
                        val face: String,
                        @SerialName("uid")
                        val uid: Int,
                        @SerialName("uname")
                        val uname: String
                    )

                    @Serializable
                    data class LevelInfo(
                        @SerialName("current_exp")
                        val currentExp: Int,
                        @SerialName("current_level")
                        val currentLevel: Int,
                        @SerialName("current_min")
                        val currentMin: Int,
                        @SerialName("next_exp")
                        val nextExp: String
                    )

                    @Serializable
                    data class Pendant(
                        @SerialName("expire")
                        val expire: Int,
                        @SerialName("image")
                        val image: String,
                        @SerialName("image_enhance")
                        val imageEnhance: String,
                        @SerialName("name")
                        val name: String,
                        @SerialName("pid")
                        val pid: Int
                    )

                    @Serializable
                    data class Vip(
                        @SerialName("accessStatus")
                        val accessStatus: Int,
                        @SerialName("dueRemark")
                        val dueRemark: String,
                        @SerialName("label")
                        val label: Label,
                        @SerialName("themeType")
                        val themeType: Int,
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
                }
            }

            @Serializable
            data class Display(
                @SerialName("comment_info")
                val commentInfo: CommentInfo,
                @SerialName("emoji_info")
                val emojiInfo: EmojiInfo,
                @SerialName("origin")
                val origin: Origin,
                @SerialName("relation")
                val relation: Relation,
                @SerialName("show_tip")
                val showTip: ShowTip,
                @SerialName("topic_info")
                val topicInfo: TopicInfo
            ) {
                @Serializable
                data class CommentInfo(
                    @SerialName("comments")
                    val comments: List<Comment>,
                    @SerialName("emojis")
                    val emojis: List<Emoji>
                ) {
                    @Serializable
                    data class Comment(
                        @SerialName("content")
                        val content: String,
                        @SerialName("name")
                        val name: String,
                        @SerialName("uid")
                        val uid: Int
                    )

                    @Serializable
                    data class Emoji(
                        @SerialName("emoji_name")
                        val emojiName: String,
                        @SerialName("meta")
                        val meta: Meta,
                        @SerialName("url")
                        val url: String
                    ) {
                        @Serializable
                        data class Meta(
                            @SerialName("size")
                            val size: Int
                        )
                    }
                }

                @Serializable
                data class EmojiInfo(
                    @SerialName("emoji_details")
                    val emojiDetails: List<EmojiDetail>
                ) {
                    @Serializable
                    data class EmojiDetail(
                        @SerialName("attr")
                        val attr: Int,
                        @SerialName("emoji_name")
                        val emojiName: String,
                        @SerialName("id")
                        val id: Int,
                        @SerialName("meta")
                        val meta: Meta,
                        @SerialName("mtime")
                        val mtime: Int,
                        @SerialName("package_id")
                        val packageId: Int,
                        @SerialName("state")
                        val state: Int,
                        @SerialName("text")
                        val text: String,
                        @SerialName("type")
                        val type: Int,
                        @SerialName("url")
                        val url: String
                    ) {
                        @Serializable
                        data class Meta(
                            @SerialName("size")
                            val size: Int
                        )
                    }
                }

                @Serializable
                data class Origin(
                    @SerialName("emoji_info")
                    val emojiInfo: EmojiInfo,
                    @SerialName("like_info")
                    val likeInfo: LikeInfo,
                    @SerialName("relation")
                    val relation: Relation,
                    @SerialName("show_tip")
                    val showTip: ShowTip,
                    @SerialName("topic_info")
                    val topicInfo: TopicInfo
                ) {
                    @Serializable
                    data class EmojiInfo(
                        @SerialName("emoji_details")
                        val emojiDetails: List<EmojiDetail>
                    ) {
                        @Serializable
                        data class EmojiDetail(
                            @SerialName("attr")
                            val attr: Int,
                            @SerialName("emoji_name")
                            val emojiName: String,
                            @SerialName("id")
                            val id: Int,
                            @SerialName("meta")
                            val meta: Meta,
                            @SerialName("mtime")
                            val mtime: Int,
                            @SerialName("package_id")
                            val packageId: Int,
                            @SerialName("state")
                            val state: Int,
                            @SerialName("text")
                            val text: String,
                            @SerialName("type")
                            val type: Int,
                            @SerialName("url")
                            val url: String
                        ) {
                            @Serializable
                            data class Meta(
                                @SerialName("size")
                                val size: Int
                            )
                        }
                    }

                    @Serializable
                    data class LikeInfo(
                        @SerialName("display_text")
                        val displayText: String,
                        @SerialName("like_users")
                        val likeUsers: List<LikeUser>
                    ) {
                        @Serializable
                        data class LikeUser(
                            @SerialName("uid")
                            val uid: Int,
                            @SerialName("uname")
                            val uname: String
                        )
                    }

                    @Serializable
                    data class Relation(
                        @SerialName("is_follow")
                        val isFollow: Int,
                        @SerialName("is_followed")
                        val isFollowed: Int,
                        @SerialName("status")
                        val status: Int
                    )

                    @Serializable
                    data class ShowTip(
                        @SerialName("del_tip")
                        val delTip: String
                    )

                    @Serializable
                    data class TopicInfo(
                        @SerialName("topic_details")
                        val topicDetails: List<TopicDetail>
                    ) {
                        @Serializable
                        data class TopicDetail(
                            @SerialName("is_activity")
                            val isActivity: Int,
                            @SerialName("topic_id")
                            val topicId: Int,
                            @SerialName("topic_link")
                            val topicLink: String,
                            @SerialName("topic_name")
                            val topicName: String
                        )
                    }
                }

                @Serializable
                data class Relation(
                    @SerialName("is_follow")
                    val isFollow: Int,
                    @SerialName("is_followed")
                    val isFollowed: Int,
                    @SerialName("status")
                    val status: Int
                )

                @Serializable
                data class ShowTip(
                    @SerialName("del_tip")
                    val delTip: String
                )

                @Serializable
                data class TopicInfo(
                    @SerialName("topic_details")
                    val topicDetails: List<TopicDetail>
                ) {
                    @Serializable
                    data class TopicDetail(
                        @SerialName("is_activity")
                        val isActivity: Int,
                        @SerialName("topic_id")
                        val topicId: Int,
                        @SerialName("topic_link")
                        val topicLink: String,
                        @SerialName("topic_name")
                        val topicName: String
                    )
                }
            }

            @Serializable
            data class Extra(
                @SerialName("is_space_top")
                val isSpaceTop: Int
            )
        }
    }
}