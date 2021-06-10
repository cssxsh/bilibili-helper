package xyz.cssxsh.mirai.plugin.tools

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.http.HttpHeaders.UnsafeHeadersList
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.overrideWithSupervisorJob
import org.openqa.selenium.remote.http.*
import org.openqa.selenium.remote.http.HttpClient.Factory
import java.net.URI

typealias SeleniumHttpClient = org.openqa.selenium.remote.http.HttpClient

typealias SeleniumHttpRequest = org.openqa.selenium.remote.http.HttpRequest

typealias SeleniumHttpResponse = org.openqa.selenium.remote.http.HttpResponse

private fun HttpRequestBuilder.takeFrom(request: SeleniumHttpRequest, base: URI) = apply {
    url {
        takeFrom(base.resolve(request.uri))
        request.queryParameterNames.forEach { name ->
            parameters.appendAll(name, request.getQueryParameters(name))
        }
    }
    method = HttpMethod.parse(request.method.name)
    headers {
        (request.headerNames - UnsafeHeadersList).forEach { name ->
            appendAll(name, request.getHeaders(name))
        }
    }
    request.attributeNames.forEach { name ->
        attributes.put(AttributeKey(name), request.getAttribute(name))
    }
    if (method == HttpMethod.Post) {
        body = ByteArrayContent(
            bytes = request.content.get().readAllBytes(),
            contentType = request.getHeader(HttpHeaders.ContentType)?.let(ContentType::parse)
        )
    }
}

private fun HttpResponse.toSeleniumHttpResponse() = SeleniumHttpResponse().also { response ->
    response.status = status.value
    response.content = Contents.memoize { content.toInputStream() }
    headers.forEach { name, list ->
        list.forEach { value ->
            response.addHeader(name, value)
        }
    }
}

private val KtorContext by lazy {
    Dispatchers.IO.overrideWithSupervisorJob("Selenium-HttpClient")
}

private class KtorHttpClient(private val config: ClientConfig) : SeleniumHttpClient {
    val client = HttpClient(OkHttp) {
        engine {
            proxy = config.proxy()
        }
        expectSuccess = false
        install(HttpTimeout) {
            connectTimeoutMillis = config.connectionTimeout().toMillis()
            requestTimeoutMillis = config.readTimeout().toMillis()
            socketTimeoutMillis = config.readTimeout().toMillis()
        }
        install(WebSockets)
        install(UserAgent) {
            agent = AddSeleniumUserAgent.USER_AGENT
        }
    }

    override fun execute(request: SeleniumHttpRequest?): SeleniumHttpResponse = runBlocking(KtorContext) {
        requireNotNull(request) { "SeleniumHttpRequest Not Null" }
        client.request<HttpResponse> { takeFrom(request, config.baseUri()) }.toSeleniumHttpResponse()
    }

    override fun openSocket(request: SeleniumHttpRequest?, listener: WebSocket.Listener?) = runBlocking(KtorContext) {
        requireNotNull(request) { "SeleniumHttpRequest Not Null" }
        requireNotNull(listener) { "SeleniumWebSocket.Listener Not Null" }
        val session = client.webSocketSession {
            takeFrom(request, config.baseUri())
            url {
                protocol = when(protocol) {
                    URLProtocol.HTTPS, URLProtocol.WSS -> URLProtocol.WSS
                    URLProtocol.HTTP, URLProtocol.WS -> URLProtocol.WS
                    else -> throw IllegalArgumentException("$protocol Not WebSocket")
                }
            }
        }
        KtorWebSocket(session, listener)
    }
}

@HttpClientName("ktor")
class KtorHttpClientFactory : Factory {

    private val clients = mutableListOf<HttpClient>()

    override fun cleanupIdleClients() = synchronized(clients) {
        clients.forEach { client ->
            client.close()
        }
        clients.clear()
    }

    override fun createClient(config: ClientConfig?): SeleniumHttpClient {
        requireNotNull(config) { "ClientConfig Not Null" }
        return KtorHttpClient(config).apply {
            synchronized(clients) {
                clients.add(client)
            }
        }
    }
}

class KtorWebSocket(private val session: DefaultClientWebSocketSession, private val listener: WebSocket.Listener) : WebSocket {

    init {
        session.launch(KtorContext) {
            while (isActive) {
                runCatching {
                    when(val frame = session.incoming.receive()) {
                        is Frame.Binary -> {
                            listener.onBinary(frame.data)
                        }
                        is Frame.Text -> {
                            listener.onText(frame.readText())
                        }
                        is Frame.Close -> {
                            val (code, reason) = requireNotNull(session.closeReason.await()) { "CloseReason Not Null" }
                            listener.onClose(code.toInt(), reason)
                            return@launch
                        }
                        else -> {}
                    }
                }.onFailure { cause ->
                    listener.onError(cause)
                    return@launch
                }
            }
        }
    }

    override fun close(): Unit = runBlocking(KtorContext) { session.close() }

    override fun send(message: Message?): WebSocket = apply {
        if (message != null) runBlocking(KtorContext) {
            when (message) {
                is BinaryMessage -> session.send(message.data())
                is TextMessage -> session.send(message.text())
                is CloseMessage -> session.close()
            }
        }
    }

    override fun sendBinary(data: ByteArray?): WebSocket = apply {
        if (data != null) runBlocking(KtorContext) {
            session.send(data)
        }
    }

    override fun sendText(data: CharSequence?): WebSocket = apply {
        if (data != null) runBlocking(KtorContext) {
            session.send(data.toString())
        }
    }
}