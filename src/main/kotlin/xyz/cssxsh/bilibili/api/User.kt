package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.BiliClient
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getUserInfo(
    mid: Long,
    url: String = ACC_INFO
): BiliUserInfo = json(url) {
    parameter("mid", mid)
    parameter("jsonp", "jsonp")
    parameter("tid", 0)
}