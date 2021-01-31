package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.BiliTempInfo
import xyz.cssxsh.bilibili.data.BiliVideoInfo

suspend fun BilibiliClient.getVideoInfo(
    aid: Long,
    url: String = BilibiliApi.VIDEO_INFO
): BiliVideoInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        parameter("aid", aid)
    }
}.transferTo(BiliVideoInfo.serializer())

suspend fun BilibiliClient.getVideoInfo(
    bvId: String,
    url: String = BilibiliApi.VIDEO_INFO
): BiliVideoInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        parameter("bvid", bvId)
    }
}.transferTo(BiliVideoInfo.serializer())