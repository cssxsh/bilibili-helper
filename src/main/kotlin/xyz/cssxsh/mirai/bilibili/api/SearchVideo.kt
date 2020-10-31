package xyz.cssxsh.mirai.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.mirai.bilibili.BilibiliClient
import xyz.cssxsh.mirai.bilibili.data.BiliSearchResult

suspend fun BilibiliClient.searchVideo(
    uid: Long,
    pageSize: Int = 30,
    pageNum: Int = 1
): BiliSearchResult = useHttpClient { client ->
    client.get(BilibiliApi.SEARCH_URL) {
        parameter("mid", uid)
        parameter("keyword", "")
        parameter("order", "pubdate")
        parameter("jsonp", "jsonp")
        parameter("ps", pageSize)
        parameter("pn", pageNum)
        parameter("tid", 0)
    }
}