package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.getGarbSuit(
    itemId: Long,
    url: String = SUIT_ITEMS
): GarbSuit = json(url) {
    parameter("item_id", itemId)
    parameter("part", "suit")
}