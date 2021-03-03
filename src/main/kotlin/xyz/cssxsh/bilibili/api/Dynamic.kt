package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.*

suspend fun BilibiliClient.getSpaceHistory(
    uid: Long,
    url: String = BilibiliApi.SPACE_HISTORY
): BiliDynamicList = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("visitor_uid", uid)
        parameter("host_uid", uid)
        parameter("offset_dynamic_id", 0)
        parameter("need_top", 0)
    }
}.transferTo(BiliDynamicList.serializer())

suspend fun BilibiliClient.getDynamicInfo(
    dynamicId: Long,
    url: String = BilibiliApi.GET_DYNAMIC_DETAIL
): BiliDynamicInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("dynamic_id", dynamicId)
    }
}.transferTo(BiliDynamicInfo.serializer())