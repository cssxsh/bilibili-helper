package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.*

suspend fun BilibiliClient.getVideoInfo(
    aid: Long,
    url: String = BilibiliApi.VIDEO_INFO
): BiliVideoInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        parameter("aid", aid)
    }
}.transferTo(BiliVideoInfo.serializer())

suspend fun BilibiliClient.getVideoInfo(
    bvid: String,
    url: String = BilibiliApi.VIDEO_INFO
): BiliVideoInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        parameter("bvid", bvid)
    }
}.transferTo(BiliVideoInfo.serializer())

suspend fun BilibiliClient.searchVideo(
    uid: Long,
    pageSize: Int = 30,
    pageNum: Int = 1,
    url: String = BilibiliApi.SEARCH_URL
): BiliSearchResult = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("mid", uid)
        parameter("keyword", "")
        parameter("order", "pubdate")
        parameter("jsonp", "jsonp")
        parameter("ps", pageSize)
        parameter("pn", pageNum)
        parameter("tid", 0)
    }
}.transferTo(BiliSearchResult.serializer())