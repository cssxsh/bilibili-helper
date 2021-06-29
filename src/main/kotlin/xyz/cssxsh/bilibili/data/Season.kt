package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Episode {
    val cover: String
    val index: String
    val title: String
    val episodeId: Long
    val share: String
}

interface Season {
    val cover: String
    val seasonId: Long
    val title: String
    val type: String
}

@Serializable
data class BiliSectionMedia(
    @SerialName("media")
    val media: SeasonMedia,
//    @SerialName("review")
//    val review: Review
)

@Serializable
data class SeasonMedia(
//        @SerialName("areas")
//        val areas: List<Area>,
    @SerialName("cover")
    override val cover: String,
    @SerialName("media_id")
    val mediaId: Int,
    @SerialName("new_ep")
    val new: SeasonNewEpisode,
    @SerialName("rating")
    val rating: SeasonRating? = null,
    @SerialName("season_id")
    override val seasonId: Long,
    @SerialName("share_url")
    val share: String,
    @SerialName("title")
    override val title: String,
    @SerialName("type_name")
    override val type: String
) : Season

@Serializable
data class SeasonNewEpisode(
    @SerialName("id")
    val id: Int,
    @SerialName("index")
    val index: String,
    @SerialName("index_show")
    val show: String
)

@Serializable
data class SeasonRating(
    @SerialName("count")
    val count: Int,
    @SerialName("score")
    val score: Double
)

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
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: Int
)

@Serializable
data class SeasonEpisode(
    @SerialName("aid")
    val aid: Long,
    @SerialName("badge")
    val badge: String,
//    @SerialName("badge_info")
//    val badgeInfo: BadgeInfo,
//    @SerialName("badge_type")
//    val badgeType: Int,
    @SerialName("cid")
    val cid: Int,
    @SerialName("cover")
    override val cover: String,
    @SerialName("from")
    val from: String,
    @SerialName("id")
    override val episodeId: Long,
    @SerialName("is_premiere")
    val isPremiere: Int,
    @SerialName("long_title")
    override val title: String,
    @SerialName("share_url")
    override val share: String,
//    @SerialName("status")
//    val status: Int,
    @SerialName("title")
    override val index: String,
    @SerialName("vid")
    val vid: String,
) : Episode

@Serializable
data class SeasonTimeline(
//    @SerialName("area")
//    val area: String,
//    @SerialName("arealimit")
//    val arealimit: Int,
//    @SerialName("attention")
//    val attention: Int,
//    @SerialName("bangumi_id")
//    val bangumiId: Int,
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
    val isFinish: Int,
    @SerialName("lastupdate")
    val last: Long,
//    @SerialName("lastupdate_at")
//    val lastupdateAt: String,
    @SerialName("new")
    val new: Boolean,
    @SerialName("play_count")
    val play: Int,
//    @SerialName("pub_time")
//    val pubTime: String,
    @SerialName("season_id")
    val seasonId: Long,
    @SerialName("season_status")
    val seasonStatus: Int,
//    @SerialName("spid")
//    val spid: Int,
    @SerialName("square_cover")
    val squareCover: String,
    @SerialName("title")
    override val title: String,
    @SerialName("viewRank")
    val viewRank: Int,
    @SerialName("weekday")
    val weekday: Int
) : Episode {
    override val share: String get() = "https://www.bilibili.com/bangumi/play/ep${episodeId}"
}