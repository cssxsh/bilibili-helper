package xyz.cssxsh.bilibili.data.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleSimple(
    @SerialName("categories")
    val categories: List<ArticleCategory>?,
    @SerialName("category")
    val category: ArticleCategory,
    @SerialName("id")
    val id: Int,
    @SerialName("image_urls")
    val imageUrls: List<String>,
    @SerialName("publish_time")
    val publishTime: Int,
    @SerialName("state")
    val state: Int,
    @SerialName("summary")
    val summary: String,
    @SerialName("title")
    val title: String,
    @SerialName("words")
    val words: Int
)