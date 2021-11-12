package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*

enum class SearchType {
    /**
     * 视频
     */
    VIDEO,

    /**
     * 番剧
     */
    BANGUMI {
        override val value = "media_bangumi"
    },

    /**
     * 影视
     */
    FILM_AND_TELEVISION {
        override val value = "media_ft"
    },

    /**
     * 直播间及主播
     */
    LIVE,

    /**
     * 直播间
     */
    LIVE_ROOM,

    /**
     * 主播
     */
    LIVE_USER,

    /**
     * 专栏
     */
    ARTICLE,

    /**
     * 话题
     */
    TOPIC,

    /**
     * 用户
     */
    USER {
        override val value = "bili_user"
    },

    /**
     * 相簿
     */
    PHOTO
    ;

    open val value = name.lowercase()
}

@Serializable
data class SearchResult<T>(
    @SerialName("numPages")
    val pages: Int,
    @SerialName("numResults")
    val total: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("pagesize")
    val size: Int,
    @SerialName("result")
    val result: List<T> = emptyList()
)

@Serializable
data class SearchUser(
    @SerialName("fans")
    val fans: Int,
    @SerialName("gender")
    val gender: Int,
    @SerialName("is_live")
    @Serializable(NumberToBooleanSerializer::class)
    val isLive: Boolean,
    @SerialName("is_upuser")
    @Serializable(NumberToBooleanSerializer::class)
    val isUpper: Boolean,
    @SerialName("level")
    override val level: Int,
    @SerialName("mid")
    override val mid: Long,
    @SerialName("room_id")
    val roomId: Long,
    @SerialName("type")
    val type: String,
    @SerialName("uname")
    override val name: String,
    @SerialName("upic")
    override val face: String,
    @SerialName("usign")
    override val sign: String,
    @SerialName("verify_info")
    val verify: String,
    @SerialName("videos")
    val videos: Int
) : UserInfo {
    override val live: String get() = "https://live.bilibili.com/${roomId}"
}

@Serializable
data class SearchSeason(
    @SerialName("cover")
    override val cover: String,
    @SerialName("cv")
    val cv: String,
    @SerialName("desc")
    val description: String,
    @SerialName("ep_size")
    val size: Int,
    @SerialName("eps")
    val episodes: List<SearchSeasonEpisode>? = null,
    @SerialName("is_avid")
    val isAvid: Boolean,
    @SerialName("is_follow")
    @Serializable(NumberToBooleanSerializer::class)
    val isFollow: Boolean,
    @SerialName("is_selection")
    @Serializable(NumberToBooleanSerializer::class)
    val isSelection: Boolean,
    @SerialName("media_id")
    override val mediaId: Long,
    @SerialName("media_score")
    override val rating: SearchSeasonRating? = null,
    @SerialName("pubtime")
    val published: Long,
    @SerialName("season_id")
    override val seasonId: Long,
    @SerialName("season_type")
    val seasonType: Int,
    @SerialName("season_type_name")
    override val type: String,
    @SerialName("selection_style")
    val selectionStyle: String,
    @SerialName("staff")
    val staff: String,
    @SerialName("styles")
    val styles: String,
    @SerialName("title")
    override val title: String,
    @SerialName("url")
    override val share: String,
    override val new: NewEpisode? = null
) : Media

@Serializable
data class SearchSeasonRating(
    @SerialName("user_count")
    override val count: Long,
    @SerialName("score")
    override val score: Double
) : Rating

@Serializable
data class SearchSeasonEpisode(
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
    @SerialName("url")
    override val share: String = "",
    @SerialName("title")
    override val index: String,
) : Episode