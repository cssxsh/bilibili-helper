package xyz.cssxsh.mirai.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.mirai.bilibili.BilibiliClient
import xyz.cssxsh.mirai.bilibili.data.BiliDynamicInfo

suspend fun BilibiliClient.dynamicInfo(
    uid: Long,
): BiliDynamicInfo = useHttpClient { client ->
    client.get(BilibiliApi.DYNAMIC_SVR) {
        parameter("visitor_uid", uid)
        parameter("host_uid", uid)
        parameter("offset_dynamic_id", 0)
        parameter("need_top", 0)
    }
}