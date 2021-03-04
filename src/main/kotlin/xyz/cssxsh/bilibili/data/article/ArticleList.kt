package xyz.cssxsh.bilibili.data.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleList(
    @SerialName("apply_time")
    val applyTime: String,
    @SerialName("articles_count")
    val articlesCount: Int,
    @SerialName("check_time")
    val checkTime: String,
    @SerialName("ctime")
    val createTime: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("publish_time")
    val publishTime: Int,
    @SerialName("read")
    val read: Int,
    @SerialName("reason")
    val reason: String,
    @SerialName("state")
    val state: Int,
    @SerialName("summary")
    val summary: String,
    @SerialName("update_time")
    val updateTime: Int,
    @SerialName("words")
    val words: Int
)