package xyz.cssxsh.bilibili

import java.time.*
import kotlin.properties.*

internal fun timestamp(sec: Long) = OffsetDateTime.ofInstant(Instant.ofEpochSecond(sec), ZoneOffset.systemDefault())

val VIDEO_REGEX = """(?i)(?<!\w)(av\d+|BV[0-9A-z]{8,12})""".toRegex()

val DYNAMIC_REGEX = """(?<=t\.bilibili\.com/(h5/dynamic/detail/)?|m\.bilibili\.com/dynamic/)(\d+)""".toRegex()

val ROOM_REGEX = """(?<=live\.bilibili\.com/)(\d+)""".toRegex()

val SHORT_LINK_REGEX = """(?<=b23\.tv\\?/)[0-9A-z]+""".toRegex()

val SPACE_REGEX = """(?<=space\.bilibili\.com/)(\d+)""".toRegex()

val SEASON_REGEX = """(?<=bilibili\.com/bangumi/play/ss)(\d+)""".toRegex()

val EPISODE_REGEX = """(?<=bilibili\.com/bangumi/play/ep)(\d+)""".toRegex()

val MEDIA_REGEX = """(?<=bilibili\.com/bangumi/media/md)(\d+)""".toRegex()

/**
 * 2017-07-01 00:00:00
 */
private const val DYNAMIC_START = 1498838400L

@Suppress("unused")
internal fun dynamictime(id: Long): Long = (id shr 32) + DYNAMIC_START

internal inline fun <reified T : Any, reified R> reflect() = ReadOnlyProperty<T, R> { thisRef, property ->
    thisRef::class.java.getDeclaredField(property.name).apply { isAccessible = true }.get(thisRef) as R
}
