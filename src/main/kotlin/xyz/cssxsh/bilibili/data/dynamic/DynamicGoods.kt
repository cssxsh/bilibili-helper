package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicGoods(
    @SerialName("head_icon")
    val icon: String = "",
    @SerialName("head_text")
    val head: String = "",
    @SerialName("items")
    val items: List<Item>,
    @SerialName("jump_url")
    val jumpUrl: String = ""
) {
    @Serializable
    data class Item(
        @SerialName("brief")
        val brief: String = "",
        @SerialName("cover")
        val cover: String = "",
        @SerialName("id")
        val id: Long,
        @SerialName("jump_desc")
        val description: String = "",
        @SerialName("jump_url")
        val jumpUrl: String = "",
        @SerialName("name")
        val name: String,
        @SerialName("price")
        val price: String
    )
}