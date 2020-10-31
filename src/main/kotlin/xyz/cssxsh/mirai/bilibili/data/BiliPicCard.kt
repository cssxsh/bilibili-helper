package xyz.cssxsh.mirai.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliPicCard(
    @SerialName("item")
    val item: Item,
    @SerialName("user")
    val user: User,
    @SerialName("activity_infos")
    val activityInfos: JsonElement? = null,
    @SerialName("extension")
    val extension: JsonElement? = null
) {
    companion object {
        const val TYPE = 2
    }

    @Serializable
    data class Item(
        @SerialName("at_control")
        val atControl: String,
        @SerialName("category")
        val category: String,
        @SerialName("ctrl")
        val ctrl: String = "",
        @SerialName("description")
        val description: String,
        @SerialName("id")
        val id: Int,
        @SerialName("is_fav")
        val isFav: Int,
        @SerialName("pictures")
        val pictures: List<Picture>,
        @SerialName("pictures_count")
        val picturesCount: Int,
        @SerialName("reply")
        val reply: Int,
        @SerialName("role")
        val role: List<JsonElement>,
        @SerialName("settings")
        val settings: Settings,
        @SerialName("source")
        val source: List<JsonElement>,
        @SerialName("title")
        val title: String,
        @SerialName("upload_time")
        val uploadTime: Int
    ) {
        @Serializable
        data class Picture(
            @SerialName("img_height")
            val imgHeight: Int,
            @SerialName("img_size")
            val imgSize: Int,
            @SerialName("img_src")
            val imgSrc: String,
            @SerialName("img_width")
            val imgWidth: Int
        )

        @Serializable
        data class Settings(
            @SerialName("copy_forbidden")
            val copyForbidden: Int
        )
    }

    @Serializable
    data class User(
        @SerialName("head_url")
        val headUrl: String,
        @SerialName("name")
        val name: String,
        @SerialName("uid")
        val uid: Int,
        @SerialName("vip")
        val vip: Vip
    ) {
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