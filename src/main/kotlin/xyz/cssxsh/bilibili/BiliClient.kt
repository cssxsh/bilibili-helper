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
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import xyz.cssxsh.bilibili.api.*

open class BiliClient : Closeable {
    companion object {
        val Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        }

        val DefaultIgnore: suspend (Throwable) -> Boolean = { it is IOException || it is HttpRequestTimeoutException }
    }

    override fun close() = clients.forEach { it.close() }

    protected open val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore

    val storage = AcceptAllCookiesStorage()

    protected open val timeout = 5_000L

    protected open fun client() = HttpClient(OkHttp) {
        defaultRequest {
            header(HttpHeaders.Origin, SPACE)
            header(HttpHeaders.Referrer, SPACE)
        }
        Json {
            serializer = KotlinxSerializer(Json)
        }
        install(HttpTimeout) {
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
        }
        install(HttpCookies) {
            storage = this@BiliClient.storage
        }
        BrowserUserAgent()
        ContentEncoding()
        engine {
            config {
                hostnameVerifier { _, _ -> true }
            }
        }
    }

    protected open val clients = MutableList(3) { client() }

    internal open val mutex = BiliApiMutex(10 * 1000L)

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            runCatching {
                block(clients.random())
            }.onFailure {
                if (ignore(it).not()) throw it
            }.onSuccess {
                return@supervisorScope it
            }
        }
        throw CancellationException()
    }
}