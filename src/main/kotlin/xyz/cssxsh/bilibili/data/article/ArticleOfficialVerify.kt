package xyz.cssxsh.bilibili.data.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleOfficialVerify(
    @SerialName("desc")
    val description: String,
    @SerialName("type")
    val type: Int
)