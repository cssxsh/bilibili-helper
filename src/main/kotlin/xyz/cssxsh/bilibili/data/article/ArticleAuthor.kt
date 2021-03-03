package xyz.cssxsh.bilibili.data.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class ArticleAuthor(
    @SerialName("face")
    val face: String,
    @SerialName("mid")
    val uid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("nameplate")
    val nameplate: UserNameplate,
    @SerialName("official_verify")
    val official: ArticleOfficialVerify,
    @SerialName("pendant")
    val pendant: UserPendant,
    @SerialName("vip")
    private val vip: JsonObject
)