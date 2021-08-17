package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getSpaceHistory(
    uid: Long,
    url: String = SPACE_HISTORY
): BiliDynamicList = json(url) {
    parameter("visitor_uid", uid)
    parameter("host_uid", uid)
    parameter("offset_dynamic_id", 0)
    parameter("need_top", 0)
}

suspend fun BiliClient.getDynamicInfo(
    dynamicId: Long,
    url: String = GET_DYNAMIC_DETAIL
): BiliDynamicInfo = json(url) {
    parameter("dynamic_id", dynamicId)
}