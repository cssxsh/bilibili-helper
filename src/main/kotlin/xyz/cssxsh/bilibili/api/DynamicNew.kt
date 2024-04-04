package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getDynamicAll(
    uid: Long,
    url: String = DYNAMIC_ALL
): BiliDynamicSpace = json(url) {
    parameter("host_mid", uid)
    parameter("timezone_offset", -480)
}

suspend fun BiliClient.getDynamicDetail(
    dynamicId: Long,
    url: String = DYNAMIC_DETAIL
): BiliDynamicDetail = json(url) {
    parameter("id", dynamicId)
    parameter("timezone_offset", -480)
}