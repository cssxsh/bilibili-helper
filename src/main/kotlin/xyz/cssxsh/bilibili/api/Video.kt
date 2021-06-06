package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.BiliClient
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getVideoInfo(
    aid: Long,
    url: String = VIDEO_INFO
): BiliVideoInfo = json(url) {
    parameter("aid", aid)
}

suspend fun BiliClient.getVideoInfo(
    bvid: String,
    url: String = VIDEO_INFO
): BiliVideoInfo = json(url) {
    parameter("bvid", bvid)
}

suspend fun BiliClient.searchVideo(
    mid: Long,
    pageSize: Int = 30,
    pageNum: Int = 1,
    url: String = SEARCH_URL
): BiliSearchResult = json(url) {
    parameter("mid", mid)
    parameter("keyword", "")
    parameter("order", "pubdate")
    parameter("jsonp", "jsonp")
    parameter("ps", pageSize)
    parameter("pn", pageNum)
    parameter("tid", 0)
}