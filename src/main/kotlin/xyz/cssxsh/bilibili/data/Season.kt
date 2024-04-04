package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.*
import java.time.*

sealed interface Episode : Entry {
    val cover: String
    val index: String
    val title: String
    val episodeId: Long
    val share: String
}

sealed interface Season {
    val cover: String
    val seasonId: Long
    val title: String
    val type: String

    val link get() = "https://www.bilibili.com/bangumi/play/ss${seasonId}"
}

sealed interface Media : Season, Entry {
    val mediaId: Long
    val share: String
    val new: NewEpisode?
    val rating: Rating?
}

sealed interface Rating : Entry {
    val count: Long
    val score: Double
}

sealed interface NewEpisode {
    val id: Long
    val index: String
    val show: String
}

@Serializable
data class BiliSectionMedia(
    @SerialName("media")
    val media: SeasonMedia
)

@Serializable
data class SeasonMedia(
    @SerialName("cover")
    override val cover: String,
    @SerialName("media_id")
    override val mediaId: Long,
    @SerialName("new_ep")
    override val new: MediaNewEpisode? = null,
    @SerialName("rating")
    override val rating: SeasonRating? = null,
    @SerialName("season_id")
    override val seasonId: Long,
    @SerialName("share_url")
    override val share: String,
    @SerialName("title")
    override val title: String,
    @SerialName("type_name")
    override val type: String,
    @SerialName("type")
    val typeId: Long,
    @SerialName("horizontal_picture")
    val horizontalPicture: String? = null,
    @SerialName("areas")
    val areas: List<SeasonArea> = emptyList()
) : Media

@Serializable
data class MediaNewEpisode(
    @SerialName("id")
    override val id: Long,
    @SerialName("index")
    override val index: String,
    @SerialName("index_show")
    override val show: String
) : NewEpisode

@Serializable
data class SeasonRating(
    @SerialName("count")
    override val count: Long,
    @SerialName("score")
    override val score: Double
) : Rating

@Serializable
data class BiliSeasonSection(
    @SerialName("main_section")
    val mainSection: SeasonSection,
    @SerialName("section")
    val section: List<SeasonSection>
)

@Serializable
data class SeasonSection(
    @SerialName("episodes")
    val episodes: List<SeasonEpisode>,
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int
) : Entry

@Serializable
data class SeasonEpisode(
    @SerialName("aid")
    val aid: Long,
    @SerialName("bvid")
    val bvid: String? = null,
    @SerialName("cover")
    override val cover: String,
    @SerialName("from")
    val from: String = "",
    @SerialName("id")
    override val episodeId: Long,
    @SerialName("is_premiere")
    @Serializable(NumberToBooleanSerializer::class)
    val isPremiere: Boolean = false,
    @SerialName("long_title")
    override val title: String,
    @SerialName("share_url")
    override val share: String,
    @SerialName("title")
    override val index: String,
    @Contextual
    @SerialName("pub_time")
    val published: Long? = null,
    @SerialName("dimension")
    val dimension: VideoDimension? = null,
    @SerialName("vid")
    val vid: String? = null,
    @SerialName("duration")
    val duration: Long = 0,
    @SerialName("enable_vt")
    val enableVT: Boolean = false,
    @SerialName("ep_id")
    val epId: Long = 0,
    @SerialName("is_view_hide")
    val isViewHide: Boolean = false,
    @SerialName("link")
    val link: String? = null,
    @SerialName("pv")
    val pv: Long = 0,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("short_link")
    val shortLink: String? = null,
    @SerialName("subtitle")
    val subtitle: String? = null
) : Episode {
    val datetime: OffsetDateTime? get() = if (published == null) null else timestamp(published)
}

@Serializable
data class SeasonTimeline(
    @SerialName("bgmcount")
    override val index: String,
    @SerialName("cover")
    override val cover: String,
    @SerialName("danmaku_count")
    val danmaku: Int,
    @SerialName("ep_id")
    override val episodeId: Long,
    @SerialName("favorites")
    val favorites: Int,
    @SerialName("is_finish")
    @Serializable(NumberToBooleanSerializer::class)
    val isFinish: Boolean,
    @SerialName("lastupdate")
    val last: Long,
    @SerialName("new")
    val new: Boolean,
    @SerialName("play_count")
    val play: Int,
    @SerialName("season_id")
    override val seasonId: Long,
    @SerialName("season_status")
    val seasonStatus: Int,
    @SerialName("square_cover")
    val squareCover: String,
    @SerialName("title")
    override val title: String,
    @SerialName("type")
    override val type: String = "番剧",
    @SerialName("viewRank")
    val viewRank: Int,
    @SerialName("weekday")
    val weekday: Int,
    @SerialName("area")
    val area: String = "",
    @SerialName("attention")
    val attention: Int = 0,
    @SerialName("bangumi_id")
    val bangumiId: Long = 0,
    @SerialName("lastupdate_at")
    val lastUpdateAt: String? = null,
    @SerialName("pub_time")
    val published: String? = null,
    @SerialName("spid")
    val spId: Long = 0,
) : Season, Episode, WithDateTime {
    override val share: String get() = "https://www.bilibili.com/bangumi/play/ep${episodeId}"
    override val datetime: OffsetDateTime get() = timestamp(last)
}

@Serializable
data class BiliSeasonInfo(
    @SerialName("cover")
    override val cover: String,
    @SerialName("episodes")
    val episodes: List<SeasonEpisode>,
    @SerialName("evaluate")
    val evaluate: String,
    @SerialName("link")
    override val link: String,
    @SerialName("media_id")
    override val mediaId: Long,
    @SerialName("new_ep")
    override val new: SeasonNewEpisode? = null,
    @SerialName("rating")
    override val rating: SeasonRating? = null,
    @SerialName("season_id")
    override val seasonId: Long,
    @SerialName("share_url")
    override val share: String,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    override val title: String,
    @SerialName("total")
    val total: Long,
    @SerialName("up_info")
    val upper: UpperSimple? = null
) : Media {
    override val type: String get() = episodes.first().from.uppercase()
}

@Serializable
data class UpperSimple(
    @SerialName("avatar")
    val avatar: String,
    @SerialName("mid")
    val mid: Long,
    @SerialName("uname")
    override val uname: String = ""
) : Owner {
    override val uid: Long get() = mid
    override val face: String get() = avatar
}

@Serializable
data class SeasonNewEpisode(
    @SerialName("id")
    override val id: Long,
    @SerialName("title")
    override val index: String,
    @SerialName("desc")
    override val show: String,
    @SerialName("is_new")
    @Serializable(NumberToBooleanSerializer::class)
    val isNew: Boolean = false
) : NewEpisode

@Serializable
data class SeasonArea(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String
)