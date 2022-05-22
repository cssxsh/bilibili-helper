package xyz.cssxsh.mirai.bilibili.command

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BilibiliInfoCommandTest {

    private suspend fun Url.getLocation() = HttpClient {
        followRedirects = false
        expectSuccess = false
    }.use {
        it.head<HttpMessage>(this).headers[HttpHeaders.Location]
    }

    @Test
    fun shortLinkTest(): Unit = runBlocking {
        Url("https://b23.tv/TDWmcE").getLocation().let {
            assertNotNull(it)
            println(it)
        }
    }
}