package xyz.cssxsh.mirai.plugin.command

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import xyz.cssxsh.mirai.plugin.command.BilibiliInfoCommand.DYNAMIC_REGEX
import xyz.cssxsh.mirai.plugin.command.BilibiliInfoCommand.VIDEO_REGEX

internal class BilibiliInfoCommandTest {

    @Test
    fun regexTest(): Unit = runBlocking {
        assertEquals("BV1iz4y1y78x", VIDEO_REGEX.find("https://www.bilibili.com/video/BV1iz4y1y78x")?.value)
        assertEquals("BV1iz4y1y78x", VIDEO_REGEX.find("https://m.bilibili.com/video/BV1iz4y1y78x")?.value)
        assertEquals("450055453856015371", DYNAMIC_REGEX.find("https://t.bilibili.com/450055453856015371")?.value)
        assertEquals("450055453856015371", DYNAMIC_REGEX.find("https://t.bilibili.com/h5/dynamic/detail/450055453856015371")?.value)
    }

    private suspend fun Url.getLocation() = HttpClient {
        followRedirects = false
        expectSuccess = false
    }.use {
        it.head<HttpMessage>(this).headers[HttpHeaders.Location]
    }

    @Test
    fun shortLinkTest(): Unit = runBlocking {
        Url("https://b23.tv/TDWmcE").getLocation().let {
            println(it)
        }
    }
}