package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.data.*

suspend fun BilibiliClient.getRoomInfo(
    roomId: Long,
    url: String = BilibiliApi.ROOM_INIT
): BiliRoomInfo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        header(HttpHeaders.Origin, BilibiliApi.SPACE)
        header(HttpHeaders.Referrer, BilibiliApi.SPACE)

        parameter("id", roomId)
    }
}.transferTo(BiliRoomInfo.serializer())

suspend fun BilibiliClient.getOffLiveList(
    roomId: Long,
    count: Int,
    timestamp: Long = System.currentTimeMillis() / 1_000,
    url: String = BilibiliApi.OFF_LIVE_LIST
): BiliLiveOff = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        parameter("room_id", roomId)
        parameter("count", count)
        parameter("rnd", timestamp)
    }
}.transferTo(BiliLiveOff.serializer())

suspend fun BilibiliClient.getRoundPlayVideo(
    roomId: Long,
    timestamp: Long = System.currentTimeMillis(),
    type: String = "flv",
    url: String = BilibiliApi.ROUND_PLAY_VIDEO
): BiliRoundPlayVideo = useHttpClient { client ->
    client.get<BiliTempInfo>(url) {
        parameter("room_id", roomId)
        parameter("a", timestamp)
        parameter("type", type)
    }
}.transferTo(BiliRoundPlayVideo.serializer())