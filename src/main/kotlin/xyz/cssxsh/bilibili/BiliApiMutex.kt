package xyz.cssxsh.bilibili

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

class BiliApiMutex(private val interval: Long) : Mutex by Mutex() {
    private var last = System.currentTimeMillis()

    suspend fun wait(): Unit = withLock {
        while (System.currentTimeMillis() - last <= interval) {
            delay(1000L)
        }
        last = System.currentTimeMillis()
    }
}