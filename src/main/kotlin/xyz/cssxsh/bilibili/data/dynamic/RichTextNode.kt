package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
sealed class RichTextNode {
    abstract val original: String
    abstract val text: String

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_TEXT")
    data class Text(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_AT")
    data class At(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("rid")
        val rid: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_LOTTERY")
    data class Lottery(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("rid")
        val rid: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_VOTE")
    data class Vote(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("rid")
        val rid: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_TOPIC")
    data class Topic(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("jump_url")
        val jumpUrl: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_GOODS")
    data class Goods(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("rid")
        val rid: String,
        @SerialName("goods")
        val goods: GoodsInfo,
        @SerialName("icon_name")
        val icon: String,
        @SerialName("jump_url")
        val jumpUrl: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_BV")
    data class Video(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("rid")
        val rid: String,
        @SerialName("jump_url")
        val jumpUrl: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_AV")
    data class RICH_TEXT_NODE_TYPE_AV(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("rid")
        val rid: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_EMOJI")
    data class Emoji(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("emoji")
        val emoji: EmojiInfo
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_MAIL")
    data class RICH_TEXT_NODE_TYPE_MAIL(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_WEB")
    data class Web(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String,
        @SerialName("jump_url")
        val jumpUrl: String,
        @SerialName("style")
        val style: String?
    ) : RichTextNode()

    @Serializable
    @SerialName("RICH_TEXT_NODE_TYPE_OGV_SEASON")
    data class RICH_TEXT_NODE_TYPE_OGV_SEASON(
        @SerialName("orig_text")
        override val original: String,
        @SerialName("text")
        override val text: String
    ) : RichTextNode()
}