package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getArticleInfo(
    cid: Long,
    url: String = ARTICLE_INFO
): BiliArticleInfo = json(url) {
    parameter("id", cid)
}