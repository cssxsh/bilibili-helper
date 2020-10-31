package xyz.cssxsh.mirai.bilibili

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class BilibiliClient(initCookies: Map<String, String> = emptyMap()) {

    private val cookiesStorage = AcceptAllCookiesStorage().apply {
        runBlocking {
            initCookies.forEach { (name, value) ->
                addCookie("https://www.bilibili.com/", Cookie(name = name, value = value))
            }
        }
    }

    companion object {
        private val KOTLINX_SERIALIZER = KotlinxSerializer(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        })
    }

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KOTLINX_SERIALIZER
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            requestTimeoutMillis = 180_000
        }
        install(HttpCookies) {
            default {
                storage = cookiesStorage
            }
        }
        BrowserUserAgent()
        ContentEncoding {
            gzip()
            deflate()
            identity()
        }
    }.use { block(it) }
}