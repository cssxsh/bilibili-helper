package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.BiliRoomInfo
import xyz.cssxsh.bilibili.data.BiliTempInfo

suspend fun BilibiliClient.roomInfo(
    roomId: Long,
    url: String = BilibiliApi.ROOM_INIT
): BiliRoomInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("id", roomId)
    }.transferTo(BiliRoomInfo.serializer())
}