package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getUserInfo(
    uid: Long,
    url: String = SPACE_INFO
): BiliUserInfo = json(url) {
    parameter("mid", uid)
    parameter("token", "")
    parameter("platform", "web")
    parameter("web_location", 1550101)
}