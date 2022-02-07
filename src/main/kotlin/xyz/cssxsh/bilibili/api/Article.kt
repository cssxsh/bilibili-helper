package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getArticleList(
    cid: Long,
    url: String = ARTICLE_LIST_INFO
): BiliArticleList = json(url) {
    parameter("id", cid)
}

suspend fun BiliClient.getArticleView(
    cid: Long,
    url: String = ARTICLE_VIEW_INFO
): BiliArticleView = json<BiliArticleView>(url) {
    parameter("id", cid)
}.copy(id = cid)