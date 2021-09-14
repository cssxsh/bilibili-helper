package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
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
        while (isActive) {
            logger.info { "${type}缓存清理任务开始运行，间隔${interval}h" }
            type.withLock {
                val now = System.currentTimeMillis()
                type.directory.listFiles { dir ->
                    dir.listFiles().orEmpty().all { file -> now - file.lastModified() > expires * HOUR && file.delete() } && dir.delete()
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