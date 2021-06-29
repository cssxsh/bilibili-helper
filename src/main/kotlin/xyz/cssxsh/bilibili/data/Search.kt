package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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
//    @SerialName("cost_time")
//    val costTime: CostTime,
//    @SerialName("egg_hit")
//    val eggHit: Int,
//    @SerialName("exp_list")
//    val expList: Map<Int,Boolean>,
    @SerialName("numPages")
    val pages: Int,
    @SerialName("numResults")
    val total: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("pagesize")
    val size: Int,
    @SerialName("result")
    val result: List<T> = emptyList(),
//    @SerialName("rqt_type")
//    val rqtType: String,
//    @SerialName("seid")
//    val seid: String,
//    @SerialName("show_column")
//    val showColumn: Int,
//    @SerialName("suggest_keyword")
//    val suggestKeyword: String
)

@Serializable
data class SearchUser(
    @SerialName("fans")
    val fans: Int,
    @SerialName("gender")
    val gender: Int,
//    @SerialName("hit_columns")
//    val hitColumns: List<String>,
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
//    @SerialName("official_verify")
//    val officialVerify: OfficialVerify,
//    @SerialName("res")
//    val list: List<VideoSimple>,
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
): UserInfo {
    override val live: String get() = "https://live.bilibili.com/${roomId}"
}

@Serializable
data class SearchSeason(
//    @SerialName("areas")
//    val areas: String,
//    @SerialName("badges")
//    val badges: List<SearchSeason.Badge>,
//    @SerialName("button_text")
//    val buttonText: String,
    @SerialName("corner")
    val corner: Int,
    @SerialName("cover")
    override val cover: String,
    @SerialName("cv")
    val cv: String,
    @SerialName("desc")
    val description: String,
//    @SerialName("display_info")
//    val displayInfo: List<SearchSeason.DisplayInfo>,
    @SerialName("ep_size")
    val size: Int,
//    @SerialName("eps")
//    val eps: List<JsonElement>,
//    @SerialName("fix_pubtime_str")
//    val fixPubtimeStr: String,
//    @SerialName("goto_url")
//    val gotoUrl: String,
//    @SerialName("hit_columns")
//    val hitColumns: List<String>,
//    @SerialName("hit_epids")
//    val hitEpids: String,
    @SerialName("is_avid")
    val isAvid: Boolean,
    @SerialName("is_follow")
    val isFollow: Int,
    @SerialName("is_selection")
    val isSelection: Int,
    @SerialName("media_id")
    val mediaId: Int,
    @SerialName("media_mode")
    val mediaMode: Int,
    @SerialName("media_score")
    val mediaScore: JsonObject? = null,
    @SerialName("media_type")
    val mediaType: Int,
//    @SerialName("org_title")
//    val orgTitle: String,
//    @SerialName("pgc_season_id")
//    val pgcSeasonId: Int,
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
//    @SerialName("type")
//    override val type: String,
    @SerialName("url")
    val url: String
) : Season