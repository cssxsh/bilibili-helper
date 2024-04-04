package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class ModuleAuthorVip(
    @SerialName("avatar_subscript")
    @Serializable(NumberToBooleanSerializer::class)
    val subscript: Boolean = false,
    @SerialName("avatar_subscript_url")
    val subscriptUrl: String = "",
    @SerialName("due_date")
    val due: Long = 0,
    @SerialName("label")
    val label: Label,
    @SerialName("nickname_color")
    val nicknameColor: String,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("theme_type")
    val theme: Int = 0,
    @SerialName("type")
    val type: Int
) {
    @Serializable
    data class Label(
        @SerialName("bg_color")
        val backgroundColor: String,
        @SerialName("bg_style")
        val backgroundStyle: Int = 0,
        @SerialName("border_color")
        val borderColor: String,
        @SerialName("img_label_uri_hans")
        val imageLabelUriHans: String = "",
        @SerialName("img_label_uri_hans_static")
        val imageLabelUriHansStatic: String = "",
        @SerialName("img_label_uri_hant")
        val imageLabelUriHant: String = "",
        @SerialName("img_label_uri_hant_static")
        val imageLabelUriHantStatic: String = "",
        @SerialName("label_theme")
        val labelTheme: String,
        @SerialName("path")
        val path: String = "",
        @SerialName("text")
        val text: String,
        @SerialName("text_color")
        val textColor: String,
        @SerialName("use_img_label")
        val useImgLabel: Boolean
    )
}