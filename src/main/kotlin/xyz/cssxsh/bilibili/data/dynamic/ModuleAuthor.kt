package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*
import java.time.*

@Serializable
sealed class ModuleAuthor {
    abstract val face: String
    abstract val label: String
    abstract val mid: Long
    abstract val name: String
    abstract val timestamp: Instant

    @Serializable
    @SerialName("AUTHOR_TYPE_NORMAL")
    data class Normal(
        @SerialName("face")
        override val face: String,
        @SerialName("face_nft")
        val faceNft: Boolean = false,
        @SerialName("following")
        val following: Boolean? = null,
        @SerialName("jump_url")
        val jumpUrl: String = "",
        @SerialName("label")
        override val label: String,
        @SerialName("mid")
        override val mid: Long,
        @SerialName("name")
        override val name: String,
        @SerialName("pub_action")
        val action: String = "",
        @SerialName("pub_location_text")
        val location: String = "",
        @SerialName("pub_time")
        val time: String = "",
        @SerialName("pub_ts")
        @Serializable(InstantSerializer::class)
        override val timestamp: Instant,
        @SerialName("avatar")
        val avatar: ModuleAuthorAvatar? = null,
        @SerialName("official_verify")
        val official: ModuleAuthorOfficialVerify? = null,
        @SerialName("decorate")
        val decorate: ModuleAuthorDecorate? = null,
        @SerialName("pendant")
        val pendant: ModuleAuthorPendant? = null,
        @SerialName("vip")
        val vip: ModuleAuthorVip? = null
    ) : ModuleAuthor()

    @Serializable
    @SerialName("AUTHOR_TYPE_PGC")
    data class Episode(
        @SerialName("face")
        override val face: String,
        @SerialName("face_nft")
        val isNFT: Boolean = false,
        @SerialName("following")
        val following: Boolean? = null,
        @SerialName("jump_url")
        val jumpUrl: String = "",
        @SerialName("label")
        override val label: String,
        @SerialName("mid")
        override val mid: Long,
        @SerialName("name")
        override val name: String,
        @SerialName("pub_action")
        val action: String = "",
        @SerialName("pub_time")
        val time: String = "",
        @SerialName("pub_ts")
        @Serializable(InstantSerializer::class)
        override val timestamp: Instant,
    ) : ModuleAuthor()

    @Serializable
    @SerialName("AUTHOR_TYPE_UGC_SEASON")
    data class Season(
        @SerialName("face")
        override val face: String,
        @SerialName("face_nft")
        val faceNft: Boolean = false,
        @SerialName("following")
        val following: Boolean? = null,
        @SerialName("jump_url")
        val jumpUrl: String = "",
        @SerialName("label")
        override val label: String,
        @SerialName("mid")
        override val mid: Long,
        @SerialName("name")
        override val name: String,
        @SerialName("pub_action")
        val action: String = "",
        @SerialName("pub_time")
        val time: String = "",
        @SerialName("pub_ts")
        @Serializable(InstantSerializer::class)
        override val timestamp: Instant,
    ) : ModuleAuthor()
}