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

interface Media : Season {
    val mediaId: Long
    val share: String
    val new: NewEpisode?
    val rating: Rating?
}

interface Rating {
    val count: Long
    val score: Double
}

interface NewEpisode {
    val id: Long
    val index: String
    val show: String
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
    override val mediaId: Long,
    @SerialName("new_ep")
    override val new: MediaNewEpisode,
    @SerialName("rating")
    override val rating: SeasonRating? = null,
    @SerialName("season_id")
    override val seasonId: Long,
    @SerialName("share_url")
    override val share: String,
    @SerialName("title")
    override val title: String,
    @SerialName("type_name")
    override val type: String
) : Media

@Serializable
data class MediaNewEpisode(
    @SerialName("id")
    override val id: Long,
    @SerialName("index")
    override val index: String,
    @SerialName("index_show")
    override val show: String
): NewEpisode

@Serializable
data class SeasonRating(
    @SerialName("count")
    override val count: Long,
    @SerialName("score")
    override val score: Double
): Rating

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
)

@Serializable
data class SeasonEpisode(
    @SerialName("aid")
    val aid: Long,
//    @SerialName("badge")
//    val badge: String,
//    @SerialName("badge_info")
//    val badgeInfo: BadgeInfo,
//    @SerialName("badge_type")
//    val badgeType: Int,
//    @SerialName("cid")
//    val cid: Long,
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
//    @SerialName("status")
//    val status: Int,
    @SerialName("title")
    override val index: String,
//    @SerialName("vid")
//    val vid: String,
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
    @Serializable(NumberToBooleanSerializer::class)
    val isFinish: Boolean,
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
    override val seasonId: Long,
    @SerialName("season_status")
    val seasonStatus: Int,
//    @SerialName("spid")
//    val spid: Int,
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
) : Season, Episode {
    override val share: String get() = "https://www.bilibili.com/bangumi/play/ep${episodeId}"
}

@Serializable
data class BiliSeasonInfo(
//    @SerialName("activity")
//    val activity: Activity,
//    @SerialName("alias")
//    val alias: String,
//    @SerialName("bkg_cover")
//    val bkgCover: String,
    @SerialName("cover")
    override val cover: String,
    @SerialName("episodes")
    val episodes: List<SeasonEpisode>,
    @SerialName("evaluate")
    val evaluate: String,
//    @SerialName("freya")
//    val freya: Freya,
//    @SerialName("jp_title")
//    val jpTitle: String,
    @SerialName("link")
    val link: String,
    @SerialName("media_id")
    override val mediaId: Long,
//    @SerialName("mode")
//    val mode: Int,
    @SerialName("new_ep")
    override val new: SeasonNewEpisode,
//    @SerialName("payment")
//    val payment: Payment,
//    @SerialName("positive")
//    val positive: Positive,
//    @SerialName("publish")
//    val publish: Publish,
    @SerialName("rating")
    override val rating: SeasonRating? = null,
//    @SerialName("record")
//    val record: String,
//    @SerialName("rights")
//    val rights: Rights,
    @SerialName("season_id")
    override val seasonId: Long,
//    @SerialName("season_title")
//    val seasonTitle: String,
//    @SerialName("seasons")
//    val seasons: List<Season>,
//    @SerialName("section")
//    val section: List<Section>,
//    @SerialName("series")
//    val series: Series,
//    @SerialName("share_copy")
//    val shareCopy: String,
//    @SerialName("share_sub_title")
//    val shareSubTitle: String,
    @SerialName("share_url")
    override val share: String,
//    @SerialName("show")
//    val show: Show,
//    @SerialName("square_cover")
//    val squareCover: String,
//    @SerialName("stat")
//    val stat: Stat,
//    @SerialName("status")
//    val status: Int,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    override val title: String,
    @SerialName("total")
    val total: Long,
    @SerialName("up_info")
    val upper: UpperSimple
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
    val uname: String = ""
)

@Serializable
data class SeasonNewEpisode(
    @SerialName("id")
    override val id: Long,
    @SerialName("title")
    override val index: String,
    @SerialName("desc")
    override val show: String
): NewEpisode