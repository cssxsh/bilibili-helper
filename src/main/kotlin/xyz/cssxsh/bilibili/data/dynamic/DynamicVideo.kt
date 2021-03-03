package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.video.*
import xyz.cssxsh.bilibili.data.user.*

@Serializable
data class DynamicVideo(
    @SerialName("aid")
    val aid: Int,
    @SerialName("attribute")
    private val attribute: Int? = null,
    @SerialName("cid")
    val cid: Int,
    @SerialName("copyright")
    val copyright: Int,
    @SerialName("ctime")
    val createdTime: Long,
    @SerialName("desc")
    val description: String,
    @SerialName("dimension")
    val dimension: VideoDimension,
    @SerialName("duration")
    val duration: Int,
    @SerialName("dynamic")
    val dynamic: String = "",
    @SerialName("jump_url")
    val jumpUrl: String,
    @SerialName("mission_id")
    private val missionId: Long? = null,
    @SerialName("owner")
    val owner: VideoOwner,
    @SerialName("pic")
    val picture: String,
    @SerialName("player_info")
    private val playerInfo: JsonObject,
    @SerialName("pubdate")
    val pubdate: Long,
    @SerialName("rights")
    private val rights: Map<String, Int> = emptyMap(),
    @SerialName("state")
    private val state: Int,
    @SerialName("stat")
    val status: VideoStatus,
    @SerialName("tid")
    val tid: Long,
    @SerialName("title")
    val title: String,
    @SerialName("tname")
    val type: String,
    @SerialName("videos")
    val videos: Int
)
