package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
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