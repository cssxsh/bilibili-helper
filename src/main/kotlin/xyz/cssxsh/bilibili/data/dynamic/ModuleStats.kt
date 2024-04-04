package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleStats(
    @SerialName("comment")
    val comment: Item,
    @SerialName("forward")
    val forward: Item,
    @SerialName("like")
    val like: Item
) {
    @Serializable
    data class Item(
        @SerialName("count")
        val count: Int,
        @SerialName("forbidden")
        val forbidden: Boolean,
        @SerialName("status")
        val status: Boolean = false,
        @SerialName("hidden")
        val hidden: Boolean = false,
    ) {
        companion object {
            val Empty = Item(
                count = 0,
                forbidden = false,
                status = false,
                hidden = false
            )
        }
    }

    companion object {
        val Empty = ModuleStats(
            comment = Item.Empty,
            forward = Item.Empty,
            like = Item.Empty
        )
    }
}