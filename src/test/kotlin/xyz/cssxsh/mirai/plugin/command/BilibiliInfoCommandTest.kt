package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BilibiliInfoCommandTest {

    private val VIDEO_REGEX = """(?<=https://(m|www)\.bilibili\.com/video/)?(av\d+|BV[0-9A-z]{10})""".toRegex()

    private val DYNAMIC_REGEX = """(?<=https://t\.bilibili\.com/(h5/dynamic/detail/)?)([0-9]{18})""".toRegex()

    @Test
    fun testRegex(): Unit = runBlocking {
        assertEquals("BV1iz4y1y78x", VIDEO_REGEX.find("https://www.bilibili.com/video/BV1iz4y1y78x")?.value)
        assertEquals("BV1iz4y1y78x", VIDEO_REGEX.find("https://m.bilibili.com/video/BV1iz4y1y78x")?.value)
        assertEquals("450055453856015371", DYNAMIC_REGEX.find("https://t.bilibili.com/450055453856015371")?.value)
        assertEquals("450055453856015371", DYNAMIC_REGEX.find("https://t.bilibili.com/h5/dynamic/detail/450055453856015371")?.value)
    }
}