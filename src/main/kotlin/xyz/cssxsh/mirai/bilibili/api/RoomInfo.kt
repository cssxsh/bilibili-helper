package xyz.cssxsh.mirai.bilibili.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.mirai.bilibili.BilibiliClient
import xyz.cssxsh.mirai.bilibili.data.BiliRoomInfo

suspend fun BilibiliClient.roomInfo(
    id: Long,
    url: String = BilibiliApi.ROOM_INIT
): BiliRoomInfo = useHttpClient { client ->
    client.get(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("id", id)
    }
}