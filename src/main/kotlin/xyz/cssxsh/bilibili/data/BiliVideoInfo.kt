package xyz.cssxsh.bilibili.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliVideoInfo(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val videoData: VideoData,
    @SerialName("message")
    val message: String,
    @SerialName("ttl")
    val ttl: Int
) {

    @Serializable
    data class VideoData(
        @SerialName("aid")
        val aid: Int,
        @SerialName("attribute")
        val attribute: Int? = null,
        @SerialName("bvid")
        val bvId: String,
        @SerialName("cid")
        val cid: Int,
        @SerialName("copyright")
        val copyright: Int,
        @SerialName("ctime")
        val ctime: Long,
        @SerialName("desc")
        val desc: String,
        @SerialName("dimension")
        val dimension: BiliDimension,
        @SerialName("duration")
        val duration: Int,
        @SerialName("dynamic")
        val dynamic: String = "",
        @SerialName("label")
        val label: Label? = null,
        @SerialName("mission_id")
        val missionId: Int = 0,
        @SerialName("no_cache")
        val noCache: Boolean,
        @SerialName("owner")
        val owner: BiliOwner,
        @SerialName("pages")
        val pages: List<Page>,
        @SerialName("pic")
        val pic: String,
        @SerialName("pubdate")
        val pubDate: Long,
        @SerialName("redirect_url")
        val redirectUrl: String? = null,
        @SerialName("rights")
        val rights: Map<String, Int>,
        @SerialName("staff")
        val staff: List<Staff>? = null,
        @SerialName("stat")
        val stat: BiliVideoState,
        @SerialName("state")
        val state: Int,
        @SerialName("subtitle")
        val subtitle: Subtitle,
        @SerialName("tid")
        val tid: Long,
        @SerialName("title")
        val title: String,
        @SerialName("tname")
        val typeName: String,
        @SerialName("videos")
        val videos: Int
    ) {

        @Serializable
        data class Label(
            @SerialName("type")
            val type: Int
        )

        @Serializable
        data class Page(
            @SerialName("cid")
            val cid: Int,
            @SerialName("dimension")
            val dimension: BiliDimension,
            @SerialName("duration")
            val duration: Int,
            @SerialName("from")
            val from: String,
            @SerialName("page")
            val page: Int,
            @SerialName("part")
            val part: String,
            @SerialName("vid")
            val vid: String,
            @SerialName("weblink")
            val weblink: String
        )

        @Serializable
        data class Staff(
            @SerialName("face")
            val face: String,
            @SerialName("follower")
            val follower: Int,
            @SerialName("label_style")
            val labelStyle: Int,
            @SerialName("mid")
            val mid: Int,
            @SerialName("name")
            val name: String,
            @SerialName("official")
            val official: BiliOfficial,
            @SerialName("title")
            val title: String,
            @SerialName("vip")
            val vip: Vip
        ) {

            @Serializable
            data class Vip(
                @SerialName("status")
                val status: Int,
                @SerialName("theme_type")
                val themeType: Int,
                @SerialName("type")
                val type: Int,
                @SerialName("vip_pay_type")
                val vipPayType: Int
            )
        }

        @Serializable
        data class Subtitle(
            @SerialName("allow_submit")
            val allowSubmit: Boolean,
            @SerialName("list")
            val list: List<SubtitleItem>
        ) {

            @Serializable
            data class SubtitleItem(
                @SerialName("id")
                val id: Long,
                @SerialName("lan")
                val language: String,
                @SerialName("lan_doc")
                val languageDoc: String,
                @SerialName("is_lock")
                val is_lock: Boolean,
                @SerialName("author_mid")
                val authorMid: Long,
                @SerialName("subtitle_url")
                val subtitleUrl: String,
                @SerialName("author")
                val author: Author
            ) {

                @Serializable
                data class Author(
                    @SerialName("mid")
                    val mid: Long,
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
                    val isFakeAccount: Int,
                    @SerialName("is_deleted")
                    val isDeleted: Int
                )
            }
        }
    }
}