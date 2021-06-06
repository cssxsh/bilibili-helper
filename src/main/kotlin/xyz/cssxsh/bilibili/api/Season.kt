package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.BiliClient
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getSeasonMedia(
    mediaId: Long,
    url: String = SEASON_MEDIA_INFO
): BiliSectionMedia = json(url) {
    parameter("media_id", mediaId)
}

suspend fun BiliClient.getSeasonSection(
    seasonId: Long,
    url: String = SEASON_SECTION
): BiliSeasonSection = json(url) {
    parameter("season_id", seasonId)
}

suspend fun BiliClient.getSeasonTimeline(
    url: String = BANGUMI_TIMELINE
): List<SeasonTimeline> = json(url) {
    //
}
