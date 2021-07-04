package xyz.cssxsh.bilibili

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import xyz.cssxsh.bilibili.api.SPACE
import java.io.IOException

class BiliClient(val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore) {
    companion object {
        val Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        }

        val DefaultIgnore: suspend (Throwable) -> Boolean = { it is IOException || it is HttpRequestTimeoutException }
    }

    private val cookiesStorage = AcceptAllCookiesStorage()

    private fun client() = HttpClient(OkHttp) {
        defaultRequest {
            header(HttpHeaders.Origin, SPACE)
            header(HttpHeaders.Referrer, SPACE)
        }
        Json {
            serializer = KotlinxSerializer(Json)
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 5_000
            connectTimeoutMillis = 5_000
            requestTimeoutMillis = 5_000
        }
        install(HttpCookies) {
            storage = cookiesStorage
        }
        BrowserUserAgent()
        ContentEncoding()
    }

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        client().use {
            while (isActive) {
                runCatching {
                    block(it)
                }.onFailure {
                    if (ignore(it).not()) throw it
                }.onSuccess {
                    return@use it
                }
            }
            throw CancellationException()
        }
    }
}