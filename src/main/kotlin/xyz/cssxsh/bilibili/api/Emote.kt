package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getEmotePanel(
    business: EmoteBusiness = EmoteBusiness.dynamic,
    url: String = EMOTE_PANEL
): EmotePanel = json(url) {
    parameter("business", business)
}

suspend fun BiliClient.getEmotePackage(
    vararg id: Long,
    business: EmoteBusiness = EmoteBusiness.dynamic,
    url: String = EMOTE_PACKAGE
): EmotePackage = json(url) {
    parameter("ids", id.joinToString(","))
    parameter("business", business)
}