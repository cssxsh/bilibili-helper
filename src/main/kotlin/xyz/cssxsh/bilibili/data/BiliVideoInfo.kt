package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.video.*

@Serializable
data class BiliVideoInfo(
    @SerialName("aid")
    val aid: Int,
    @SerialName("attribute")
    private val attribute: Int? = null,
    @SerialName("bvid")
    val bvid: String,
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
    @SerialName("label")
    private val label: JsonObject? = null,
    @SerialName("mission_id")
    private val missionId: Long? = null,
    @SerialName("no_cache")
    private val noCache: Boolean,
    @SerialName("owner")
    val owner: VideoOwner,
    @SerialName("pages")
    val pages: List<VideoPage> = emptyList(),
    @SerialName("pic")
    val picture: String,
    @SerialName("pubdate")
    val pubdate: Long,
    @SerialName("redirect_url")
    val redirect: String? = null,
    @SerialName("rights")
    private val rights: Map<String, Int>,
    @SerialName("season_id")
    private val seasonId: Int? = null,
    @SerialName("staff")
    val staff: List<VideoStaff> = emptyList(),
    @SerialName("stat")
    val status: VideoStatus,
    @SerialName("state")
    private val state: Int,
    @SerialName("stein_guide_cid")
    private val steinGuideCid: Int? = null,
    @SerialName("subtitle")
    val subtitle: VideoSubtitle,
    @SerialName("tid")
    val tid: Long,
    @SerialName("title")
    val title: String,
    @SerialName("tname")
    val type: String,
    @SerialName("videos")
    val videos: Int,
    @SerialName("user_garb")
    private val userGarb: JsonObject? = null,
    @SerialName("ugc_season")
    private val ugcSeason: JsonElement? = null
)
