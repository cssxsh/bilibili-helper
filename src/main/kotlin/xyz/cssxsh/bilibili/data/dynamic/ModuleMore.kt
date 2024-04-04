package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleMore(
    @SerialName("three_point_items")
    val threePointItems: List<Item> = emptyList()
) {
    @Serializable
    data class Item(
        @SerialName("label")
        val label: String = "",
        @SerialName("type")
        val type: String = ""
    )

    companion object {
        val Empty = ModuleMore(
            threePointItems = emptyList()
        )
    }
}