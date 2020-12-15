package xyz.cssxsh.bilibili

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.internal.http2.StreamResetException
import java.io.EOFException
import java.net.ConnectException
import javax.net.ssl.SSLException

class BilibiliClient(initCookies: Map<String, String>) {

    private val cookiesStorage = AcceptAllCookiesStorage().apply {
        runBlocking {
            initCookies.forEach { (name, value) ->
                addCookie("https://www.bilibili.com/", Cookie(name = name, value = value, domain = ".bilibili.com"))
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

    suspend fun <T> useHttpClient(
        ignore: (exception: Throwable) -> Boolean = { throwable ->
            when (throwable) {
                is SSLException,
                is EOFException,
                is ConnectException,
                is SocketTimeoutException,
                is HttpRequestTimeoutException,
                is StreamResetException,
                -> {
                    true
                }
                else -> when(throwable.message) {
                    "Required SETTINGS preface not received" -> true
                    else -> false
                }
            }
        },
        block: suspend (HttpClient) -> T
    ): T = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KOTLINX_SERIALIZER
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 10_000
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 60_000
        }
        install(HttpCookies) {
            storage = cookiesStorage
        }
        BrowserUserAgent()
        ContentEncoding {
            gzip()
            deflate()
            identity()
        }
    }.use {
        var result: T? = null
        while (result === null) {
            result = runCatching { block(it) }.onFailure {
                if (ignore(it).not()) throw it
            }.getOrNull()
        }
        result
    }
}