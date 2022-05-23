package xyz.cssxsh.mirai.bilibili.data

import kotlinx.serialization.*
import xyz.cssxsh.mirai.bilibili.*
import java.time.*

@Serializable
class BiliInterval(
    @SerialName("start")
    @Serializable(with = LocalTimeSerializer::class)
    override val start: LocalTime,
    @SerialName("end")
    @Serializable(with = LocalTimeSerializer::class)
    override val endInclusive: LocalTime
) : ClosedRange<LocalTime> {
    override fun contains(value: LocalTime): Boolean {
        return when {
            start < endInclusive -> value >= start && value <= endInclusive
            start > endInclusive -> value >= start || value <= endInclusive
            else -> false
        }
    }

    override fun isEmpty(): Boolean = (start == endInclusive)

    override fun toString(): String = "$start~$endInclusive"
}