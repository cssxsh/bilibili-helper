package xyz.cssxsh.bilibili.data.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class VideoSubtitle(
    @SerialName("allow_submit")
    val allowSubmit: Boolean,
    @SerialName("list")
    val list: List<SubtitleItem>
) {

    @Serializable
    data class SubtitleItem(
        @SerialName("author_mid")
        val uid: Long? = null,
        @SerialName("author")
        val author: Author? = null,
        @SerialName("id")
        val id: Long,
        @SerialName("lan")
        val language: String,
        @SerialName("lan_doc")
        val languageDocument: String,
        @SerialName("is_lock")
        val isLock: Boolean,
        @SerialName("subtitle_url")
        val subtitleUrl: String
    ) {

        @Serializable
        data class Author(
            @SerialName("mid")
            val uid: Long,
            @SerialName("name")
            val name: String,
            @SerialName("sex")
            val sex: String,
            @SerialName("face")
            val face: String,
            @SerialName("sign")
            val sign: String,
            @SerialName("rank")
            val rank: Int,
            @SerialName("birthday")
            val birthday: Int,
            @SerialName("is_fake_account")
            @Serializable(with = NumberToBooleanSerializer::class)
            val isFakeAccount: Boolean,
            @SerialName("is_deleted")
            @Serializable(with = NumberToBooleanSerializer::class)
            val isDeleted: Boolean
        )
    }
}