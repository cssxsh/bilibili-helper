package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.data.*

object BiliCleaner : CoroutineScope by BiliHelperPlugin.childScope("BiliCleaner") {

    private val interval by BiliCleanerConfig::interval

    private fun clean(type: CacheType, hour: Int) = launch(SupervisorJob()) {
        if (hour <= 0) {
            logger.info { "${type}缓存清理跳过" }
            return@launch
        }
        logger.info { "${type}缓存清理任务开始运行，间隔${hour}m" }
        while (isActive) {
            delay(hour * 60 * 60 * 1000L)
            type.withLock {
                ImageCache.resolve(type.name).walkBottomUp().onLeave { it.delete() }
            }
        }
    }

    fun start() {
        interval.forEach { (type, duration) -> clean(type, duration) }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}