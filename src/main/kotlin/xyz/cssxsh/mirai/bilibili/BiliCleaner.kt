package xyz.cssxsh.mirai.bilibili

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.bilibili.data.*
import kotlin.coroutines.*

object BiliCleaner : CoroutineScope {

    override val coroutineContext: CoroutineContext =
        CoroutineName(name = "BiliCleaner") + SupervisorJob() + CoroutineExceptionHandler { context, throwable ->
            logger.warning({ "$throwable in $context" }, throwable)
        }

    private val interval by BiliCleanerConfig::interval

    private val expires by BiliCleanerConfig::expires

    private const val HOUR = 60 * 60 * 1000L

    private fun clean(type: CacheType, interval: Int, expires: Int) = launch {
        if (interval <= 0) {
            logger.info { "${type}缓存清理跳过" }
            return@launch
        }
        while (isActive) {
            logger.info { "${type}缓存清理任务开始运行，间隔${interval}h" }
            type.withLock {
                val now = System.currentTimeMillis()
                type.directory.listFiles { folder ->
                    folder.listFiles().orEmpty()
                        .all { file -> now - file.lastModified() > expires * HOUR && file.delete() }
                        && folder.delete()
                }
            }
            delay(interval * HOUR)
        }
    }

    fun start() {
        for (type in CacheType.values()) {
            clean(type = type, interval = interval.getValue(type), expires = expires.getValue(type))
        }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}