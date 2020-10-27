package xyz.cssxsh.mirai.plugin.data


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
    val originUser: OriginUser,
    @SerialName("user")
    val user: User,
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
        val rpId: Long,
        @SerialName("timestamp")
        val timestamp: Int,
        @SerialName("uid")
        val uid: Int
    )

    @Serializable
    data class OriginUser(
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

    @Serializable
    data class User(
        @SerialName("face")
        val face: String,
        @SerialName("uid")
        val uid: Int,
        @SerialName("uname")
        val uname: String
    )
}