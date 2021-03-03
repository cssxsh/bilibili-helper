package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class DynamicPicture(
    @SerialName("item")
    val item: Item,
    @SerialName("user")
    val user: UserSimple,
    @SerialName("activity_infos")
    private val activityInfos: JsonObject? = null,
    @SerialName("extension")
    private val extension: JsonObject? = null
) {

    @Serializable
    data class Info(
        @SerialName("img_height")
        val height: Int,
        @SerialName("img_size")
        val size: Double? = null,
        @SerialName("img_src")
        val source: String,
        @SerialName("img_width")
        val width: Int
    )

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
        val pictures: List<Info>,
        @SerialName("pictures_count")
        val picturesCount: Int,
        @SerialName("reply")
        val reply: Int,
        @SerialName("role")
        val role: List<JsonObject>,
        @SerialName("settings")
        val settings: JsonObject,
        @SerialName("source")
        val source: List<JsonObject>,
        @SerialName("title")
        val title: String,
        @SerialName("upload_time")
        val uploadTime: Long
    )
}