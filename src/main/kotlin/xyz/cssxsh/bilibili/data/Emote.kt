package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*

@Serializable
data class EmotePanel(
    @SerialName("all_packages")
    val all: List<EmoteItem>,
    @SerialName("user_panel_packages")
    val user: List<EmoteItem>
)

@Serializable
data class EmotePackage(
    @SerialName("packages")
    val packages: List<EmoteItem>
)

@Serializable
data class EmoteItem(
    @SerialName("emote")
    val emote: List<EmojiDetail>,
    @SerialName("id")
    val id: Int,
    @SerialName("mtime")
    val mtime: Long,
    @SerialName("text")
    val text: String,
    @SerialName("type")
    val type: Int,
    @SerialName("url")
    val url: String
)

@Serializable
data class EmojiDetail(
    @SerialName("id")
    val id: Int,
    @SerialName("mtime")
    val mtime: Long,
    @SerialName("package_id")
    val packageId: Int,
    @SerialName("text")
    val text: String,
    @SerialName("type")
    val type: Int,
    @SerialName("url")
    val url: String
)

@Serializable
@Suppress("EnumEntryName")
enum class EmoteBusiness { reply, dynamic }