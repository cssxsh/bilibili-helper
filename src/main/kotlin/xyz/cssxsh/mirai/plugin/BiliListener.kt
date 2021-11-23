package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.event.*

@OptIn(ConsoleExperimentalApi::class)
internal object BiliListener : CoroutineScope by BiliHelperPlugin.childScope("BiliListener") {

    fun subscribe(): Unit = with(globalEventChannel()) {
        subscribeMessages {
            for ((regex, replier) in UrlRepliers) {
                regex findingReply replier
            }
        }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}