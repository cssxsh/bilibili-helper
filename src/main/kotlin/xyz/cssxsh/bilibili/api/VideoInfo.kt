package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.BiliVideoInfo

suspend fun BilibiliClient.videoInfo(
    aid: Long,
    url: String = BilibiliApi.VIDEO_INFO
): BiliVideoInfo = useHttpClient { client ->
    client.get(url) {
        parameter("aid", aid)
    }
}

suspend fun BilibiliClient.videoInfo(
    bvId: String,
    url: String = BilibiliApi.VIDEO_INFO
): BiliVideoInfo = useHttpClient { client ->
    client.get(url) {
        parameter("bvid", bvId)
    }
}