package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.BiliDynamicDetail
import xyz.cssxsh.bilibili.data.BiliDynamicInfo

suspend fun BilibiliClient.spaceHistory(
    uid: Long,
    url: String = BilibiliApi.SPACE_HISTORY
): BiliDynamicInfo = useHttpClient { client ->
    client.get(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("visitor_uid", uid)
        parameter("host_uid", uid)
        parameter("offset_dynamic_id", 0)
        parameter("need_top", 0)
    }
}

suspend fun BilibiliClient.getDynamicDetail(
    dynamicId: Long,
    url: String = BilibiliApi.GET_DYNAMIC_DETAIL
): BiliDynamicDetail = useHttpClient { client ->
    client.get(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("dynamic_id", dynamicId)
    }
}