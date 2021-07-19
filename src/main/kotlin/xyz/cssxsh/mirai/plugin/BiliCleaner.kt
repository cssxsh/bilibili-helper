package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.data.*

object BiliCleaner : CoroutineScope by BiliHelperPlugin.childScope("BiliCleaner") {

    private val interval by BiliCleanerConfig::interval

    private val expires by BiliCleanerConfig::expires

    private const val HOUR = 60 * 60 * 1000L

    private fun clean(type: CacheType, interval: Int, expires: Int) = launch(SupervisorJob()) {
        if (interval <= 0) {
            logger.info { "${type}缓存清理跳过" }
            return@launch
        }
        logger.info { "${type}缓存清理任务开始运行，间隔${interval}h" }
        while (isActive) {
            type.withLock {
                val now = System.currentTimeMillis()
                ImageCache.resolve(type.name).listFiles().orEmpty().forEach { dir ->
                    dir.listFiles().orEmpty().forEach { file ->
                        if (now - file.lastModified() > expires * HOUR) file.delete()
                    }
                    if (dir.list().isNullOrEmpty()) dir.delete()
                }
            }
            delay(interval * HOUR)
        }
    }

    fun start() {
        CacheType.values().forEach { type ->
            clean(type = type, interval = interval.getValue(type), expires = expires.getValue(type))
        }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}