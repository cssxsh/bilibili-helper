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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import xyz.cssxsh.bilibili.api.SPACE
import java.io.IOException
import kotlin.coroutines.CoroutineContext

open class BiliClient: CoroutineScope, Closeable {
    companion object {
        val Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        }

        val DefaultIgnore: suspend (Throwable) -> Boolean = { it is IOException || it is HttpRequestTimeoutException }
    }

    override val coroutineContext: CoroutineContext
        get() = client.coroutineContext

    override fun close() = client.close()

    protected open val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore

    private val cookiesStorage = AcceptAllCookiesStorage()

    private val client = HttpClient(OkHttp) {
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
        engine {
            config {
                hostnameVerifier { _, _ -> true }
            }
        }
    }

    internal open val mutex = BiliApiMutex(10 * 1000L)

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            runCatching {
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