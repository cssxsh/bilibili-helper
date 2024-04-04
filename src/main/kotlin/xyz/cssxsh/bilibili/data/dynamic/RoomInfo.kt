package xyz.cssxsh.bilibili.data.dynamic

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable
data class RoomInfo(
    @SerialName("live_play_info")
    val play: LivePlay,
    @SerialName("live_record_info")
    val record: LiveRecord?,
    @SerialName("type")
    val type: Int
) {
    object AsStringSerializer : KSerializer<RoomInfo> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: RoomInfo) {
            val json = Json { serializersModule = encoder.serializersModule }
            val text = json.encodeToString(serializer = serializer(), value = value)
            encoder.encodeString(value = text)
        }

        override fun deserialize(decoder: Decoder): RoomInfo {
            val json = Json { serializersModule = decoder.serializersModule }
            val text = decoder.decodeString()
            return json.decodeFromString(deserializer = serializer(), string = text)
        }
    }

    @Serializable
    data class LivePlay(
        @SerialName("area_id")
        val areaId: Int,
        @SerialName("area_name")
        val areaName: String,
        @SerialName("cover")
        val cover: String,
        @SerialName("link")
        val link: String,
        @SerialName("live_id")
        val liveId: Long,
        @SerialName("live_screen_type")
        val liveScreenType: Int,
        @SerialName("live_start_time")
        val liveStartTime: Long,
        @SerialName("live_status")
        @Serializable(NumberToBooleanSerializer::class)
        val liveStatus: Boolean,
        @SerialName("online")
        val online: Long,
        @SerialName("parent_area_id")
        val parentAreaId: Int,
        @SerialName("parent_area_name")
        val parentAreaName: String,
        @SerialName("pendants")
        val pendants: JsonElement, // TODO
        @SerialName("play_type")
        val playType: Int,
        @SerialName("room_id")
        val roomId: Long,
        @SerialName("room_paid_type")
        val roomPaidType: Int = 0,
        @SerialName("room_type")
        val roomType: Int,
        @SerialName("title")
        val title: String,
        @SerialName("uid")
        val uid: Long,
        @SerialName("watched_show")
        val watchedShow: WatchedShow = WatchedShow()
    )

    @Serializable
    data class WatchedShow(
        @SerialName("icon")
        val icon: String = "",
        @SerialName("icon_location")
        val iconLocation: String = "",
        @SerialName("icon_web")
        val iconWeb: String = "",
        @SerialName("num")
        val num: Int = 0,
        @SerialName("switch")
        val switch: Boolean = false,
        @SerialName("text_large")
        val textLarge: String = "",
        @SerialName("text_small")
        val textSmall: String = ""
    )

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
        val start: String,
        @SerialName("title")
        val title: String,
        @SerialName("uid")
        val uid: Long,
        @SerialName("uname")
        val uname: String
    )
}