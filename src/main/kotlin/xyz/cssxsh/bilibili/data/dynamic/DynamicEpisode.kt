package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.bilibili.data.NumberToBooleanSerializer

@Serializable
data class DynamicEpisode(
    @SerialName("aid")
    val aid: Int,
    @SerialName("apiSeasonInfo")
    val info: ApiSeasonInfo,
    @SerialName("bullet_count")
    val bulletCount: Int,
    @SerialName("cover")
    val cover: String,
    @SerialName("episode_id")
    val episodeId: Int,
    @SerialName("index")
    val index: String,
    @SerialName("index_title")
    val indexTitle: String,
    @SerialName("new_desc")
    val newDesc: String,
    @SerialName("online_finish")
    val onlineFinish: Int,
    @SerialName("play_count")
    val playCount: Int,
    @SerialName("reply_count")
    val replyCount: Int,
    @SerialName("url")
    val url: String
) {
    @Serializable
    data class ApiSeasonInfo(
        @SerialName("bgm_type")
        val bgmType: Int, // XXX
        @SerialName("cover")
        val cover: String,
        @SerialName("is_finish")
        @Serializable(with = NumberToBooleanSerializer::class)
        val isFinish: Boolean,
        @SerialName("season_id")
        val seasonId: Int,
        @SerialName("title")
        val title: String,
        @SerialName("total_count")
        val totalCount: Int,
        @SerialName("ts")
        val timestamp: Long,
        @SerialName("type_name")
        val typeName: String
    )
}