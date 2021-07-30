package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.BiliClient
import xyz.cssxsh.bilibili.data.GarbSuit

suspend fun BiliClient.getGarbSuit(
    itemId: Long,
    url: String = SUIT_ITEMS
): GarbSuit = json(url) {
    parameter("item_id", itemId)
    parameter("part", "suit")
}