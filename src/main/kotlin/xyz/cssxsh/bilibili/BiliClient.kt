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
import io.ktor.http.Cookie
import io.ktor.utils.io.core.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import okhttp3.*
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

    override fun close() = client.close()

    protected open val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore

    val storage = AcceptAllCookiesStorage()

    val AcceptAllCookiesStorage.container: MutableList<Cookie> by reflect()

    protected open val timeout = 15_000L

    protected open val client = HttpClient(OkHttp) {
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
                // XXX okhttp3.internal.http2.StreamResetException
                protocols(listOf(Protocol.HTTP_1_1))
            }
        }
    }

    protected open val mutex = BiliApiMutex(10 * 1000L)

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            runCatching {
                mutex.wait()
                block(client)
            }.onFailure {
                if (ignore(it).not()) throw it
            }.onSuccess {
                return@supervisorScope it
            }
        }
        throw CancellationException()
    }
}