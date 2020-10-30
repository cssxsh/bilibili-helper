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
import kotlinx.serialization.json.JsonElement

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
        val attentions: Attentions? = null,
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
            @Serializable(JsonTextSerializer::class)
            val card: JsonElement,
            @SerialName("desc")
            val desc: Desc,
            @SerialName("display")
            val display: JsonElement,
            @SerialName("extend_json")
            @Serializable(JsonTextSerializer::class)
            val extendJson: JsonElement,
            @SerialName("extra")
            val extra: Extra,
            @SerialName("activity_infos")
            val activityInfos: JsonElement? = null,
            @SerialName("extension")
            val extension: JsonElement? = null
        ) {
            companion object {
                object JsonTextSerializer : KSerializer<JsonElement> {
                    override fun deserialize(decoder: Decoder): JsonElement =
                        Json.decodeFromString(JsonElement.serializer(), decoder.decodeString())


                    override val descriptor: SerialDescriptor
                        get() = PrimitiveSerialDescriptor("JsonTextSerializer", PrimitiveKind.STRING)

                    override fun serialize(encoder: Encoder, value: JsonElement) {
                        encoder.encodeString(Json.encodeToString(JsonElement.serializer(), value))
                    }
                }
            }

            @Serializable
            data class Desc(
                @SerialName("acl")
                val acl: Int,
                @SerialName("comment")
                val comment: Int? = null,
                @SerialName("dynamic_id")
                val dynamicId: Long,
                @SerialName("dynamic_id_str")
                val dynamicIdStr: String,
                @SerialName("inner_id")
                val innerId: Long,
                @SerialName("is_liked")
                val isLiked: Int,
                @SerialName("like")
                val like: Int,
                @SerialName("orig_dy_id")
                val origDyId: Long,
                @SerialName("orig_dy_id_str")
                val origDyIdStr: String,
                @SerialName("orig_type")
                val origType: Int,
                @SerialName("origin")
                val origin: JsonElement? = null,
                @SerialName("pre_dy_id")
                val preDyId: Long,
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
                val sType: Int,
                @SerialName("timestamp")
                val timestamp: Long,
                @SerialName("type")
                val type: Int,
                @SerialName("uid")
                val uid: Int,
                @SerialName("uid_type")
                val uidType: Int,
                @SerialName("user_profile")
                val userProfile: UserProfile,
                @SerialName("view")
                val view: Int,
                @SerialName("bvid")
                val bvId: String? = null,
                @SerialName("previous")
                val previous: JsonElement? = null
            ) {

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
                    val vip: Vip,
                    @SerialName("decorate_card")
                    val decorate_card: JsonElement? = null
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
                        val face: String? = null,
                        @SerialName("uid")
                        val uid: Long,
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
                        val pid: Long
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
            data class Extra(
                @SerialName("is_space_top")
                val isSpaceTop: Int
            )
        }
    }
}