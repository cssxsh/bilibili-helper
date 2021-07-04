package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import kotlinx.serialization.json.decodeFromJsonElement
import xyz.cssxsh.bilibili.BiliClient
import xyz.cssxsh.bilibili.data.TempData

// Base
const val INDEX_PAGE = "https://www.bilibili.com"
const val SPACE = "https://space.bilibili.com"

// User
const val ACC_INFO = "https://api.bilibili.com/x/space/acc/info"

// Video
const val SEARCH_URL = "https://api.bilibili.com/x/space/arc/search"
const val VIDEO_INFO = "https://api.bilibili.com/x/web-interface/view"

// Dynamic
const val SPACE_HISTORY = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history"
const val GET_DYNAMIC_DETAIL = "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/get_dynamic_detail"

// Live
const val ROOM_INIT = "https://api.live.bilibili.com/room/v1/Room/room_init"
const val ROOM_INFO_OLD = "https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld"
const val OFF_LIVE_LIST = "https://api.live.bilibili.com/xlive/web-room/v1/index/getOffLiveList"
const val ROUND_PLAY_VIDEO = "https://api.live.bilibili.com/live/getRoundPlayVideo"

// Article
const val ARTICLE_INFO = "https://api.bilibili.com/x/article/listinfo"

// Music
const val MUSIC_INFO = "https://www.bilibili.com/audio/music-service-c/web/song/info"

// Media
const val SEASON_MEDIA_INFO = "https://api.bilibili.com/pgc/review/user"
const val SEASON_INFO = "https://api.bilibili.com/pgc/view/web/season"
const val SEASON_RECOMMEND = "https://api.bilibili.com/pgc/season/web/related/recommend?season_id=38234"
const val SEASON_SECTION = "https://api.bilibili.com/pgc/web/season/section"
const val SEASON_EPISODE = "https://api.bilibili.com/pgc/view/web/section/order?ep_id=395214"
const val BANGUMI_TIMELINE = "https://bangumi.bilibili.com/api/timeline_v2_global"
const val BANGUMI_TIMELINE_CN = "https://bangumi.bilibili.com/api/timeline_v2_cn"

// Tag
const val TAG_INFO = "https://api.bilibili.com/x/tag/info?tag_name=MEGALOBOX"
const val TAG_DETAIL = "https://api.bilibili.com/x/tag/detail?pn=0&ps=1&tag_id=18828932"
const val TAG_VIDEO = "https://api.bilibili.com/x/web-interface/tag/top?pn=1&ps=10&tid=18828932"

// Search
const val SEARCH_ALL = "https://api.bilibili.com/x/web-interface/search/all/v2"
const val SEARCH_TYPE = "https://api.bilibili.com/x/web-interface/search/type"

internal suspend inline fun <reified T> BiliClient.json(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): T = useHttpClient { client ->
    mutex.wait()
    with(client.get<TempData>(url, block)) {
        val temp = data ?: result
        BiliClient.Json.decodeFromJsonElement(requireNotNull(temp) { message })
    }
}