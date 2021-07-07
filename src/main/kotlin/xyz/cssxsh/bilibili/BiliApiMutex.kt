package xyz.cssxsh.bilibili

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BiliApiMutex(private val interval: Long) : Mutex by Mutex() {
    private var last = System.currentTimeMillis()

    suspend fun wait(): Unit = withLock {
        while (System.currentTimeMillis() - last <= interval) {
            delay(1000L)
        }
        last = System.currentTimeMillis()
    }
}