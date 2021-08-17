package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getUserInfo(
    uid: Long,
    url: String = ACC_INFO
): BiliUserInfo = json(url) {
    parameter("mid", uid)
    parameter("jsonp", "jsonp")
    parameter("tid", 0)
}