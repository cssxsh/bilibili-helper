package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GarbSuit(
    @SerialName("fan_user")
    val fanUser: FanUser,
    @SerialName("item")
    val item: GarbSuitItem,
    @SerialName("suit_items")
    val items: Map<String, List<GarbSuitItem>>
)

@Serializable
data class FanUser(
    @SerialName("avatar")
    val avatar: String,
    @SerialName("mid")
    val mid: Int,
    @SerialName("nickname")
    val nickname: String
)

@Serializable
data class GarbSuitItem(
    @SerialName("item_id")
    val itemId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("properties")
    val properties: JsonObject,
    @SerialName("sale_left_time")
    val saleLeftTime: Int,
    @SerialName("sale_surplus")
    val saleSurplus: Int,
    @SerialName("sale_time_end")
    val saleTimeEnd: Int,
    @SerialName("state")
    val state: String,
    @SerialName("suit_item_id")
    val suitItemId: Int,
    @SerialName("tab_id")
    val tabId: Int,
    @SerialName("items")
    val items: List<GarbSuitItem>? = null
)

val GarbSuit.emoji get() = items["emoji_package"].orEmpty()