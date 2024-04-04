package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

/**
 * [动态类型](https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/dynamic/dynamic_enum.md)
 */
@Serializable
sealed class DynamicDetail {
    abstract val id: Long?
    abstract val basic: Basic
    abstract val modules: DynamicModules
    abstract val visible: Boolean

    open val author: ModuleAuthor get() = modules.author
    open val major: DynamicMajor? get() = modules.dynamic.major
    open val describe: RichText? get() = modules.dynamic.describe

    @Serializable
    data class Basic(
        @SerialName("comment_id_str")
        val commentId: String,
        @SerialName("comment_type")
        val commentType: Int,
        @SerialName("jump_url")
        val jumpUrl: String? = null,
        @SerialName("like_icon")
        val likeIcon: LikeIcon,
        @SerialName("rid_str")
        val rid: String
    )

    @Serializable
    data class DynamicModules(
        @SerialName("module_author")
        val author: ModuleAuthor,
        @SerialName("module_dynamic")
        val dynamic: ModuleDynamic,
        @SerialName("module_more")
        val more: ModuleMore = ModuleMore.Empty,
        @SerialName("module_stat")
        val stat: ModuleStats = ModuleStats.Empty
    )

    @Serializable
    @SerialName("DYNAMIC_TYPE_NONE")
    data class None(
        @SerialName("id_str")
        override val id: Long?,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.None get() = super.major as DynamicMajor.None
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_FORWARD")
    data class Forward(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("orig")
        val original: DynamicDetail,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val describe: RichText get() = super.describe as RichText
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_AV")
    data class Video(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.Archive get() = super.major as DynamicMajor.Archive
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_WORD")
    data class Word(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.Opus? get() = super.major as? DynamicMajor.Opus
        override val describe: RichText get() = super.describe ?: major!!.content.summary
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_DRAW")
    data class Draw(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor get() = super.major as DynamicMajor
        override val describe: RichText get() = super.describe ?: (super.major as DynamicMajor.Opus).content.summary
        val items: List<DynamicPictureItem>
            get() = when (val major = super.major) {
                is DynamicMajor.Draw -> major.content.items
                is DynamicMajor.Opus -> major.content.pictures
                else -> emptyList()
            }
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_ARTICLE")
    data class Article(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor get() = super.major as DynamicMajor
        override val describe: RichText get() = super.describe ?: (super.major as DynamicMajor.Opus).content.summary
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_MUSIC")
    data class Music(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.Music get() = super.major as DynamicMajor.Music
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_COMMON_SQUARE")
    data class Sketch(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.Common get() = super.major as DynamicMajor.Common
        override val describe: RichText get() = super.describe as RichText
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_LIVE")
    data class Live(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.Live get() = super.major as DynamicMajor.Live
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_LIVE_RCMD")
    data class LiveRoom(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.LiveRoom get() = super.major as DynamicMajor.LiveRoom
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_PGC_UNION")
    data class Episode(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Episode get() = super.author as ModuleAuthor.Episode
        override val major: DynamicMajor.Episode get() = super.major as DynamicMajor.Episode
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_UGC_SEASON")
    data class Season(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Season get() = super.author as ModuleAuthor.Season
        override val major: DynamicMajor.Season get() = super.major as DynamicMajor.Season
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_MEDIALIST")
    data class MediaList(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.MediaList get() = super.major as DynamicMajor.MediaList
    }

    @Serializable
    @SerialName("DYNAMIC_TYPE_COURSES_SEASON")
    data class Courses(
        @SerialName("id_str")
        override val id: Long,
        @SerialName("basic")
        override val basic: Basic,
        @SerialName("modules")
        override val modules: DynamicModules,
        @SerialName("visible")
        override val visible: Boolean = true
    ) : DynamicDetail() {
        override val author: ModuleAuthor.Normal get() = super.author as ModuleAuthor.Normal
        override val major: DynamicMajor.Courses get() = super.major as DynamicMajor.Courses
    }
}