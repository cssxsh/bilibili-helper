package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.*
import java.time.*

sealed interface Live : Entry, Owner {
    val roomId: Long
    val title: String
    val cover: String
    val online: Long
    val liveStatus: Boolean
    val link: String

    val start: OffsetDateTime?
    override val uname: String
    override val uid: Long

    val share: String get() = link.substringBefore('?')
}

@Serializable
data class BiliRoomInfo(
    @SerialName("encrypted")
    val encrypted: Boolean,
    @SerialName("hidden_till")
    @Serializable(NumberToBooleanSerializer::class)
    val hiddenTill: Boolean,
    @SerialName("is_hidden")
    val isHidden: Boolean,
    @SerialName("is_locked")
    val isLocked: Boolean,
    @SerialName("is_portrait")
    val isPortrait: Boolean,
    @SerialName("is_sp")
    @Serializable(NumberToBooleanSerializer::class)
    val isSmartPhone: Boolean,
    @SerialName("live_status")
    val status: Int,// 0 未开播 1 开播中 2 轮播中
    @SerialName("live_time")
    val liveTime: Long,
    @SerialName("lock_till")
    @Serializable(NumberToBooleanSerializer::class)
    val lockTill: Boolean,
    @SerialName("pwd_verified")
    val passwordVerified: Boolean,
    @SerialName("room_id")
    val roomId: Long,
    @SerialName("short_id")
    val shortId: Int,
    @SerialName("uid")
    val uid: Long
) : Entry {
    val datetime: OffsetDateTime get() = timestamp(liveTime)
}

@Serializable
data class BiliRoomSimple(
    @SerialName("cover")
    override val cover: String,
    @SerialName("liveStatus")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean,
    @SerialName("online")
    override val online: Long,
    @SerialName("online_hidden")
    @Serializable(NumberToBooleanSerializer::class)
    val onlineHidden: Boolean = false,
    @SerialName("roomStatus")
    @Serializable(NumberToBooleanSerializer::class)
    val roomStatus: Boolean,
    @SerialName("roomid")
    override val roomId: Long,
    @SerialName("roundStatus")
    @Serializable(NumberToBooleanSerializer::class)
    val roundStatus: Boolean,
    @SerialName("title")
    override val title: String,
    @SerialName("url")
    override val link: String
) : Live {
    @Transient
    override var start: OffsetDateTime? = null

    @Transient
    override var uname: String = ""

    @Transient
    override var uid: Long = 0
}

@Serializable
data class BiliRoundPlayVideo(
    @SerialName("aid")
    val aid: Long,
    @SerialName("bvid")
    val bvid: String? = null,
    @SerialName("bvid_url")
    val link: String? = null,
    @SerialName("cid")
    val cid: Long,
    @SerialName("pid")
    val pid: Long,
    @SerialName("play_time")
    val time: Long,
    @SerialName("play_url")
    val url: String? = null,
    @SerialName("sequence")
    val sequence: Int,
    @SerialName("title")
    val title: String
)

@Serializable
data class BiliLiveOff(
    @SerialName("recommend")
    val recommends: List<LiveRecommend>,
    @SerialName("record_list")
    val records: List<LiveRecord>,
    @SerialName("tips")
    val tips: String
)

@Serializable
data class LiveRecommend(
    @SerialName("cover")
    override val cover: String,
    @SerialName("face")
    override val face: String,
    @SerialName("flag")
    val flag: Int,
    @SerialName("is_auto_play")
    @Serializable(NumberToBooleanSerializer::class)
    val isAutoPlay: Boolean,
    @SerialName("link")
    override val link: String,
    @SerialName("online")
    override val online: Long,
    @SerialName("roomid")
    override val roomId: Long,
    @SerialName("title")
    override val title: String,
    @SerialName("uid")
    override val uid: Long,
    @SerialName("uname")
    override val uname: String,
    @SerialName("liveStatus")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean = true,
) : Live {
    override val start: OffsetDateTime? get() = null
}

@Serializable
data class LiveRecord(
    @SerialName("bvid")
    val bvid: String,
    @SerialName("cover")
    val cover: String,
    @SerialName("is_sticky")
    @Serializable(NumberToBooleanSerializer::class)
    val isSticky: Boolean,
    @SerialName("rid")
    val roomId: String,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("title")
    val title: String,
    @SerialName("uid")
    override val uid: Long,
    @SerialName("uname")
    override val uname: String
) : Owner