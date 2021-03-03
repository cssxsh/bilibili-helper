package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DynamicMusic(
    @SerialName("author")
    val author: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("ctime")
    val ctime: Long,
    @SerialName("id")
    val id: Long,
    @SerialName("intro")
    val intro: String,
    @SerialName("playCnt")
    val playCount: Int,
    @SerialName("replyCnt")
    val replyCount: Int,
    @SerialName("schema")
    val schema: String,
    @SerialName("title")
    val title: String,
    @SerialName("typeInfo")
    val typeInfo: String,
    @SerialName("upId")
    val upId: Long,
    @SerialName("upper")
    val upper: String,
    @SerialName("upperAvatar")
    val upperAvatar: String
)