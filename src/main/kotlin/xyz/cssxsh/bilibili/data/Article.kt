package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.*
import java.time.*

sealed interface Article : Entry {
    val id: Long
    val images: List<String>
    val title: String
    val published: Long
    val summary: String get() = ""
    val status: ArticleStatus? get() = null

    val link get() = "https://www.bilibili.com/read/cv$id"
    val datetime: OffsetDateTime get() = timestamp(published)
}

@Serializable
data class BiliArticleList(
    @SerialName("last")
    val last: ArticleSimple,
    @SerialName("list")
    val list: ArticleList? = null,
    @SerialName("next")
    val next: ArticleSimple? = null,
    @SerialName("now")
    val now: Long,
    @SerialName("total")
    val total: Long
)

@Serializable
data class BiliArticleView(
    @SerialName("attention")
    val attention: Boolean,
    @SerialName("author_name")
    val authorName: String,
    @SerialName("banner_url")
    val bannerUrl: String,
    @SerialName("coin")
    val coin: Long,
    @SerialName("favorite")
    val favorite: Boolean,
    @SerialName("image_urls")
    val imageUrls: List<String>,
    @SerialName("in_list")
    val inList: Boolean,
    @SerialName("is_author")
    val isAuthor: Boolean,
    @SerialName("like")
    @Serializable(NumberToBooleanSerializer::class)
    val like: Boolean,
    @SerialName("mid")
    val mid: Long,
    @SerialName("next")
    val next: Long,
    @SerialName("origin_image_urls")
    override val images: List<String>,
    @SerialName("pre")
    val pre: Long,
    @SerialName("share_channels")
    val shareChannels: List<ShareChannel>,
    @SerialName("shareable")
    val shareable: Boolean,
    @SerialName("show_later_watch")
    val showLaterWatch: Boolean,
    @SerialName("show_small_window")
    val showSmallWindow: Boolean,
    @SerialName("stats")
    override val status: ArticleStatus,
    @SerialName("title")
    override val title: String,
    @SerialName("id")
    override val id: Long = 0,
    @SerialName("publish_time")
    override val published: Long = 0,
) : Article, Owner {
    override val uid: Long get() = mid
    override val uname: String get() = authorName

    @Serializable
    data class ShareChannel(
        @SerialName("name")
        val name: String,
        @SerialName("picture")
        val picture: String,
        @SerialName("share_channel")
        val shareChannel: String
    )
}

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
    val coin: Long = 0,
    @SerialName("dislike")
    @Serializable(NumberToBooleanSerializer::class)
    val dislike: Boolean = false,
    @SerialName("dynamic")
    val dynamic: Long = 0,
    @SerialName("favorite")
    val favorite: Long = 0,
    @SerialName("like")
    val like: Long = 0,
    @SerialName("reply")
    val reply: Long = 0,
    @SerialName("share")
    val share: Long = 0,
    @SerialName("view")
    val view: Long = 0
) : Entry

@Serializable
data class ArticleSimple(
    @SerialName("categories")
    val categories: List<ArticleCategory>? = null,
    @SerialName("category")
    val category: ArticleCategory,
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