package xyz.cssxsh.bilibili.api

import io.ktor.client.request.*
import kotlinx.coroutines.*
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.data.*

suspend fun BiliClient.login(push: (url: String) -> Unit) {
    val qrcode = json<Qrcode>(QRCODE_GENERATE) {
        // ...
    }

    push(qrcode.url)

    withTimeout(300_000) {
        while (isActive) {
            delay(3_000)
            val status = json<QrcodeStatus>(QRCODE_POLL) {
                parameter("qrcode_key", qrcode.key)
            }
            when (status.code) {
                0 -> break
                86038 -> throw IllegalStateException(status.message)
                else -> Unit
            }
        }
    }
}