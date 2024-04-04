package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*

@Serializable
data class DynamicEpisode(
    @SerialName("badge")
    val badge: DynamicBadge,
    @SerialName("cover")
    val cover: String = "",
    @SerialName("epid")
    val episodeId: Long,
    @SerialName("jump_url")
    val jumpUrl: String = "",
    @SerialName("season_id")
    val seasonId: Long,
    @SerialName("stat")
    val stat: Map<String, String> = emptyMap(),
    @SerialName("sub_type")
    val subType: Int,
    @SerialName("title")
    val title: String = "",
    @SerialName("type")
    val type: Int
)