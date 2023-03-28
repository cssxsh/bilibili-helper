package xyz.cssxsh.bilibili

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import xyz.cssxsh.bilibili.api.*

internal abstract class ApiTest {
    init {
        System.setProperty(EXCEPTION_JSON_CACHE, "./test")
        System.getProperty(JSON_IGNORE, "false")
    }

    protected val client = BiliClient()

    @org.junit.jupiter.api.BeforeEach
    fun cookie(): Unit = runBlocking {
        // home
        try {
            client.useHttpClient { http, _ -> http.get(INDEX_PAGE).bodyAsText() }
        } catch (_: Exception) {
            //
        }
        // space
        try {
            client.useHttpClient { http, _ -> http.get(SPACE).bodyAsText() }
        } catch (_: Exception) {
            //
        }
    }
}