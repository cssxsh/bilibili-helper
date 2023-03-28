package xyz.cssxsh.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.bilibili.*
import java.time.*
import java.time.format.*

sealed interface Live : Entry, Owner, WithDateTime {
    val roomId: Long
    val title: String
    val cover: String
    val online: Long
    val liveStatus: Boolean
    val link: String

    val start: OffsetDateTime?
    override val datetime: OffsetDateTime get() = start!!
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
    override val online: Long = 0,
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
    override val online: Long = 0,
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


@Serializable
data class RoomInfo(
    @SerialName("area_id")
    val areaId: Int,
    @SerialName("area_name")
    val areaName: String,
    @SerialName("background")
    val background: String,
    @SerialName("cover")
    override val cover: String,
    @SerialName("description")
    val description: String,
    @SerialName("hidden_status")
    val hiddenStatus: Int,
    @SerialName("hidden_time")
    val hiddenTime: Long,
    @SerialName("is_studio")
    val isStudio: Boolean,
    @SerialName("keyframe")
    val keyframe: String,
    @SerialName("live_screen_type")
    val liveScreenType: Int,
    @SerialName("live_start_time")
    val liveStartTime: Long,
    @SerialName("live_status")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean,
    @SerialName("lock_status")
    val lockStatus: Int,
    @SerialName("lock_time")
    val lockTime: Long,
    @SerialName("on_voice_join")
    val onVoiceJoin: Int,
    @SerialName("online")
    override val online: Long,
    @SerialName("parent_area_id")
    val parentAreaId: Int,
    @SerialName("parent_area_name")
    val parentAreaName: String,
    @SerialName("pk_status")
    val pkStatus: Int,
    @SerialName("room_id")
    override val roomId: Long,
    @SerialName("room_type")
    val roomType: Map<String, Int>,
    @SerialName("short_id")
    val shortId: Int,
    @SerialName("special_type")
    val specialType: Int,
    @SerialName("tags")
    val tags: String,
    @SerialName("title")
    override val title: String,
    @SerialName("uid")
    override val uid: Long,
    @SerialName("up_session")
    val upSession: String
) : Live {
    override val start: OffsetDateTime get() = timestamp(sec = liveStartTime)
    override val datetime: OffsetDateTime get() = timestamp(sec = liveStartTime)
    override val link: String get() = if (shortId == 0) "https://space.bilibili.com/${uid}" else "https://live.bilibili.com/${shortId}"
    override val share: String get() = "https://live.bilibili.com/${roomId}"
    override val uname: String get() = upSession
}

@Serializable
data class AnchorInfo(
    @SerialName("base_info")
    val baseInfo: BaseInfo
) {
    @Serializable
    data class BaseInfo(
        @SerialName("face")
        val face: String,
        @SerialName("gender")
        val gender: String,
        @SerialName("uname")
        val uname: String
    )
}

@Serializable
data class BiliLiveInfo(
    @SerialName("room_info")
    val roomInfo: RoomInfo,
    @SerialName("anchor_info")
    val anchorInfo: AnchorInfo
) : Live by roomInfo {
    override val face get() = anchorInfo.baseInfo.face
    override val uname get() = anchorInfo.baseInfo.uname
}

@Serializable
data class SearchLiveRoom(
    @SerialName("area")
    val area: Int,
    @SerialName("attentions")
    val attentions: Int,
    @SerialName("cate_name")
    val cateName: String,
    @SerialName("cover")
    override val cover: String,
    @SerialName("hit_columns")
    val hitColumns: List<String>,
    @SerialName("is_live_room_inline")
    @Serializable(NumberToBooleanSerializer::class)
    val isLiveRoomInline: Boolean,
    @SerialName("live_status")
    @Serializable(NumberToBooleanSerializer::class)
    override val liveStatus: Boolean,
    @SerialName("live_time")
    val liveTime: String,
    @SerialName("online")
    override val online: Long = 0,
    @SerialName("rank_index")
    val rankIndex: Int,
    @SerialName("rank_offset")
    val rankOffset: Int,
    @SerialName("rank_score")
    val rankScore: Int,
    @SerialName("roomid")
    override val roomId: Long,
    @SerialName("short_id")
    val shortId: Int,
    @SerialName("tags")
    val tags: String,
    @SerialName("title")
    override val title: String,
    @SerialName("type")
    val type: String = "",
    @SerialName("uface")
    override val face: String,
    @SerialName("uid")
    override val uid: Long,
    @SerialName("uname")
    override val uname: String,
    @SerialName("user_cover")
    val userCover: String
) : Live {
    override val link: String get() = if (shortId == 0) "https://space.bilibili.com/${uid}" else "https://live.bilibili.com/${shortId}"
    override val start: OffsetDateTime? by lazy {
        try {
            OffsetDateTime.parse(liveTime)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}