package xyz.cssxsh.bilibili

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*

class BiliApiMutex(private val interval: Long) : Mutex by Mutex() {
    private val map = HashMap<String, Long>()

    suspend fun wait(type: String): Unit = withLock {
        val last = map[type] ?: 0
        while (System.currentTimeMillis() - last <= interval) {
            delay(1000L)
        }
        map[type] = System.currentTimeMillis()
    }
}