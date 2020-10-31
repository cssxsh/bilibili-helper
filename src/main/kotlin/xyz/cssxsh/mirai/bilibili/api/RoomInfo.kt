package xyz.cssxsh.mirai.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.mirai.bilibili.BilibiliClient
import xyz.cssxsh.mirai.bilibili.data.BiliRoomInfo

suspend fun BilibiliClient.roomInfo(
    id: Long
): BiliRoomInfo = useHttpClient { client ->
    client.get(BilibiliApi.ROOM_INIT) {
        parameter("id", id)
    }
}