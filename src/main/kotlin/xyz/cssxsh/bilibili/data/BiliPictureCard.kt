package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliPictureCard(
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
        val atControl: String? = null,
        @SerialName("category")
        val category: String,
        @SerialName("ctrl")
        val ctrl: String = "",
        @SerialName("description")
        val description: String,
        @SerialName("id")
        val id: Long,
        @SerialName("is_fav")
        val isFavourite: Int,
        @SerialName("pictures")
        val pictures: List<Picture>,
        @SerialName("pictures_count")
        val picturesCount: Int,
        @SerialName("reply")
        val reply: Int,
        @SerialName("role")
        val role: List<JsonElement>,
        @SerialName("settings")
        val settings: JsonElement,
        @SerialName("source")
        val source: List<JsonElement>,
        @SerialName("title")
        val title: String,
        @SerialName("upload_time")
        val uploadTime: Long
    ) {

        @Serializable
        data class Picture(
            @SerialName("img_height")
            val imageHeight: Int,
            @SerialName("img_size")
            val imageSize: Double? = null,
            @SerialName("img_src")
            val imageSource: String,
            @SerialName("img_width")
            val imageWidth: Int
        )
    }

    @Serializable
    data class User(
        @SerialName("head_url")
        val headUrl: String? = null,
        @SerialName("name")
        val name: String,
        @SerialName("uid")
        val uid: Int,
        @SerialName("vip")
        val vip: JsonElement
    )
}