package xyz.cssxsh.bilibili.data.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleCategory(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("parent_id")
    val parentId: Int
)