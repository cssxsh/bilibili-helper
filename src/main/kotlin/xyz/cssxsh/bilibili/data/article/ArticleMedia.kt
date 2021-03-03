package xyz.cssxsh.bilibili.data.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleMedia(
    @SerialName("area")
    val area: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("media_id")
    val mediaId: Int,
    @SerialName("score")
    val score: Int,
    @SerialName("season_id")
    val seasonId: Int,
    @SerialName("spoiler")
    val spoiler: Int,
    @SerialName("title")
    val title: String,
    @SerialName("type_id")
    val typeId: Int,
    @SerialName("type_name")
    val typeName: String
)