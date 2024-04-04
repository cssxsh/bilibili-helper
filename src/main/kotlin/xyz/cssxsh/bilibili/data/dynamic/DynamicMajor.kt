package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
sealed class DynamicMajor {
    abstract val content: Any

    @Serializable
    @SerialName("MAJOR_TYPE_NONE")
    data class None(
        @SerialName("none")
        override val content: DynamicNone,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_OPUS")
    data class Opus(
        @SerialName("opus")
        override val content: DynamicOpus,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_ARCHIVE")
    data class Archive(
        @SerialName("archive")
        override val content: DynamicVideo,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_COURSES")
    data class Courses(
        @SerialName("courses")
        override val content: DynamicCourses,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_PGC")
    data class Episode(
        @SerialName("pgc")
        override val content: DynamicEpisode,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_DRAW")
    data class Draw(
        @SerialName("draw")
        override val content: DynamicPicture,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_ARTICLE")
    data class Article(
        @SerialName("article")
        override val content: String,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_MUSIC")
    data class Music(
        @SerialName("music")
        override val content: DynamicMusic,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_COMMON")
    data class Common(
        @SerialName("common")
        override val content: DynamicSketch,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_LIVE")
    data class Live(
        @SerialName("live")
        override val content: DynamicLive,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_MEDIALIST")
    data class MediaList(
        @SerialName("medialist")
        override val content: DynamicMediaList,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_LIVE_RCMD")
    data class LiveRoom(
        @SerialName("live_rcmd")
        override val content: DynamicLiveRoom,
    ) : DynamicMajor()

    @Serializable
    @SerialName("MAJOR_TYPE_UGC_SEASON")
    data class Season(
        @SerialName("ugc_season")
        override val content: DynamicVideo,
    ) : DynamicMajor()
}