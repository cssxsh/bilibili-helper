package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import xyz.cssxsh.bilibili.data.article.*

@Serializable
data class DynamicArticle(
    @SerialName("act_id")
    val actId: Int,
    @SerialName("apply_time")
    val applyTime: String,
    @SerialName("authenMark")
    private val authenMark: JsonElement?,
    @SerialName("author")
    val author: ArticleAuthor,
    @SerialName("banner_url")
    val bannerUrl: String,
    @SerialName("categories")
    val categories: List<ArticleCategory> = emptyList(),
    @SerialName("category")
    val category: ArticleCategory,
    @SerialName("check_time")
    val checkTime: String,
    @SerialName("cover_avid")
    val coverAvid: Int,
    @SerialName("ctime")
    val createdTime: Int,
    @SerialName("dispute")
    private val dispute: JsonElement?,
    @SerialName("id")
    val id: Int,
    @SerialName("image_urls")
    val imageUrls: List<String>,
    @SerialName("is_like")
    val isLike: Boolean,
    @SerialName("list")
    val infos: ArticleList,
    @SerialName("media")
    val media: ArticleMedia,
    @SerialName("origin_image_urls")
    val originImageUrls: List<String>,
    @SerialName("original")
    val original: Int,
    @SerialName("publish_time")
    val publishTime: Int,
    @SerialName("reprint")
    val reprint: Int,
    @SerialName("state")
    val state: Int,
    @SerialName("stats")
    val status: ArticleStatus,
    @SerialName("summary")
    val summary: String,
    @SerialName("template_id")
    val templateId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("top_video_info")
    private val topVideoInfo: JsonElement?,
    @SerialName("words")
    val words: Int
)