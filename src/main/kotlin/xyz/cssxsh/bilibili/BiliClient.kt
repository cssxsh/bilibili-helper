package xyz.cssxsh.bilibili

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import java.util.concurrent.*

open class BiliClient(private val timeout: Long = 15_000L) : Closeable {
    companion object {
        val Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = System.getProperty(JSON_IGNORE, "true").toBoolean()
            isLenient = true
            coerceInputValues = true
        }

        val DefaultIgnore: suspend (Throwable) -> Boolean = { it is IOException }

        internal fun getMixinKey(ae: String): String {
            val oe = arrayOf(46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49, 33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40, 61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11, 36, 20, 34, 44, 52)
            return buildString {
                for (i in oe) {
                    append(ae[i])
                    if (length >= 32) break
                }
            }
        }
    }

    override fun close() = clients.forEach { it.close() }

    protected open val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore

    val storage = AcceptAllCookiesStorage()

    val AcceptAllCookiesStorage.container: MutableList<Cookie> by reflect()

    val salt: CompletableFuture<String> = CompletableFuture()

    protected open fun client() = HttpClient(OkHttp) {
        defaultRequest {
            header(HttpHeaders.Origin, SPACE)
            header(HttpHeaders.Referrer, SPACE)
        }
        expectSuccess = true
        install(ContentNegotiation) {
            json(json = Json)
        }
        install(HttpTimeout) {
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            requestTimeoutMillis = null
        }
        install(HttpCookies) {
            storage = this@BiliClient.storage
        }
        install(UserAgent) {
            agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36"
        }
        ContentEncoding()
        engine {
            config {
                hostnameVerifier { _, _ -> true }
                // XXX okhttp3.internal.http2.StreamResetException
                // XXX unexpected end of stream with Protocol.HTTP_1_1
                // protocols(listOf(Protocol.HTTP_1_1))
            }
        }
    }

    protected open val clients = MutableList(3) { client() }

    protected open var index = 0

    protected open val mutex = BiliApiMutex(10 * 1000L)

    suspend fun <T> useHttpClient(block: suspend (HttpClient, BiliApiMutex) -> T): T = supervisorScope {
        var cause: Throwable? = null
        while (isActive) {
            try {
                return@supervisorScope block(clients[index], mutex)
            } catch (throwable: Throwable) {
                cause = throwable
                if (isActive && ignore(throwable)) {
                    index = (index + 1) % clients.size
                } else {
                    throw throwable
                }
            }
        }
        throw CancellationException(null, cause)
    }

    suspend fun salt(): String {
        val body = useHttpClient { http, _ ->
            http.get(WBI).body<JsonObject>()
        }
        val data = body.getValue("data") as JsonObject
        val images = Json.decodeFromJsonElement<WbiImages>(data.getValue("wbi_img"))
        val a = images.imgUrl.substringAfter("wbi/").substringBefore(".")
        val b = images.subUrl.substringAfter("wbi/").substringBefore(".")
        val key = getMixinKey(a + b)

        salt.complete(key)

        return key
    }
}