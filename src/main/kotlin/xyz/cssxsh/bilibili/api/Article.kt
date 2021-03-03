package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.*

suspend fun BilibiliClient.getArticleInfo(
    cid: Long,
    url: String = BilibiliApi.ARTICLE_INFO
): BiliArticleInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("id", cid)
    }
}.transferTo(BiliArticleInfo.serializer())