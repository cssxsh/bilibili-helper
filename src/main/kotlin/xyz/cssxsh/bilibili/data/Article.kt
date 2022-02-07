package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.*
import java.time.*

sealed interface Article: Entry {
    val categories: List<ArticleCategory>?
    val category: ArticleCategory
    val id: Long
    val images: List<String>
    val title: String
    val published: Long
    val summary: String

    val link get() = "https://www.bilibili.com/read/cv$id"
    val datetime: OffsetDateTime get() = timestamp(published)
}

@Serializable
data class BiliArticleInfo(
    @SerialName("last")
    val last: ArticleSimple,
    @SerialName("list")
    val list: ArticleList?,
    @SerialName("now")
    val now: Long,
    @SerialName("total")
    val total: Long
)

@Serializable
data class ArticleAuthor(
    @SerialName("face")
    override val face: String,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("nameplate")
    val nameplate: UserNameplate,
    @SerialName("official_verify")
    val official: UserOfficial
) : Owner {
    override val uid: Long get() = mid
    override val uname: String get() = name
}

@Serializable
data class ArticleStatus(
    @SerialName("coin")
    val coin: Long,
    @SerialName("dislike")
    @Serializable(NumberToBooleanSerializer::class)
    val dislike: Boolean,
    @SerialName("dynamic")
    val dynamic: Long,
    @SerialName("favorite")
    val favorite: Long,
    @SerialName("like")
    val like: Long,
    @SerialName("reply")
    val reply: Long,
    @SerialName("share")
    val share: Long,
    @SerialName("view")
    val view: Long
)

@Serializable
data class ArticleSimple(
    @SerialName("categories")
    override val categories: List<ArticleCategory>? = null,
    @SerialName("category")
    override val category: ArticleCategory,
    @SerialName("id")
    override val id: Long,
    @SerialName("image_urls")
    override val images: List<String>,
    @SerialName("publish_time")
    override val published: Long,
    @SerialName("summary")
    override val summary: String,
    @SerialName("title")
    override val title: String,
    @SerialName("words")
    val words: Int
) : Article

@Serializable
data class ArticleMedia(
    @SerialName("area")
    val area: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("media_id")
    val mediaId: Long,
    @SerialName("score")
    val score: Int,
    @SerialName("season_id")
    val seasonId: Long,
    @SerialName("spoiler")
    val spoiler: Int,
    @SerialName("title")
    val title: String,
    @SerialName("type_id")
    val typeId: Int,
    @SerialName("type_name")
    val type: String
)

@Serializable
data class ArticleList(
    @SerialName("apply_time")
    val apply: String,
    @SerialName("articles_count")
    val count: Int,
    @SerialName("check_time")
    val checked: String,
    @SerialName("ctime")
    val created: Long,
    @SerialName("id")
    val id: Long,
    @SerialName("image_url")
    val image: String,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("publish_time")
    val published: Long,
    @SerialName("read")
    val read: Int,
    @SerialName("reason")
    val reason: String,
    @SerialName("state")
    val state: Int,
    @SerialName("summary")
    val summary: String,
    @SerialName("update_time")
    val updated: Long,
    @SerialName("words")
    val words: Int
) : Owner {
    override val uid: Long get() = mid
    override val uname: String get() = name
}

@Serializable
data class ArticleCategory(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("parent_id")
    val parentId: Long
)