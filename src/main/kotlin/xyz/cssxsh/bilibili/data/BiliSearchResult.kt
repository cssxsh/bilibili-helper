package xyz.cssxsh.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.bilibili.data.video.VideoSimple

@Serializable
data class BiliSearchResult(
    @SerialName("episodic_button")
    private val episodicButton: JsonObject? = null,
    @SerialName("list")
    val list: InfoList,
    @SerialName("page")
    val page: Page
) {

    @Serializable
    data class TypeInfo(
        @SerialName("count")
        val count: Int,
        @SerialName("name")
        val name: String,
        @SerialName("tid")
        val tid: Long
    )



    @Serializable
    data class InfoList(
        @SerialName("tlist")
        val typeList: Map<Int, TypeInfo>? = null,
        @SerialName("vlist")
        val videoList: List<VideoSimple>
    )

    @Serializable
    data class Page(
        @SerialName("count")
        val count: Int,
        @SerialName("pn")
        val num: Int,
        @SerialName("ps")
        val size: Int
    )
}