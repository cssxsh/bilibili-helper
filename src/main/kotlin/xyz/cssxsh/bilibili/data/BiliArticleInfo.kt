package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import xyz.cssxsh.bilibili.data.article.ArticleList
import xyz.cssxsh.bilibili.data.article.ArticleSimple

@Serializable
data class BiliArticleInfo(
    @SerialName("last")
    val last: ArticleSimple,
    @SerialName("list")
    val list: ArticleList,
    @SerialName("next")
    private val next: JsonElement?,
    @SerialName("now")
    val now: Int,
    @SerialName("total")
    val total: Int
)