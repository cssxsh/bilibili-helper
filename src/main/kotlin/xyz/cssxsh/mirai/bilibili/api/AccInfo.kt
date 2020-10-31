package xyz.cssxsh.mirai.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.mirai.bilibili.BilibiliClient
import xyz.cssxsh.mirai.bilibili.data.BiliAccInfo

suspend fun BilibiliClient.accInfo(
    uid: Long
): BiliAccInfo = useHttpClient { client ->
    client.get(BilibiliApi.ACC_INFO) {
        parameter("mid", uid)
        parameter("jsonp", "jsonp")
        parameter("tid", 0)
    }
}