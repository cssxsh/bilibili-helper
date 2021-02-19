package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.minutes

@Serializable
data class BilibiliTaskInfo(
    @SerialName("video_last")
    val videoLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("dynamic_last")
    val dynamicLast: Long = System.currentTimeMillis() / 1_000,
    @SerialName("min_interval_millis")
    val minIntervalMillis: Long = (5).minutes.toLongMilliseconds(),
    @SerialName("max_interval_millis")
    val maxIntervalMillis: Long = (10).minutes.toLongMilliseconds(),
    @SerialName("contacts")
    val contacts: List<ContactInfo> = emptyList()
) {

    enum class ContactType {
        GROUP,
        FRIEND
    }

    @Serializable
    data class ContactInfo(
        @SerialName("id")
        val id: Long,
        @SerialName("bot")
        val bot: Long,
        @SerialName("type")
        val type: ContactType
    )

    fun getInterval() = minIntervalMillis..maxIntervalMillis
}