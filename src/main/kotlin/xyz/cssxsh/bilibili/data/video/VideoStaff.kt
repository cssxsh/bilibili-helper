package xyz.cssxsh.bilibili.data.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.user.UserOfficial

@Serializable
data class VideoStaff(
    @SerialName("face")
    val face: String,
    @SerialName("follower")
    val follower: Int,
    @SerialName("label_style")
    private val labelStyle: Int,
    @SerialName("mid")
    val mid: Long,
    @SerialName("name")
    val name: String,
    @SerialName("official")
    val official: UserOfficial,
    @SerialName("title")
    val title: String,
    @SerialName("vip")
    private val vip: JsonObject
)