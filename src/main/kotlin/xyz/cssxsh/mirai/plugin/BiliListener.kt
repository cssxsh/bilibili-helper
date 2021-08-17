package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.event.*

internal object BiliListener : CoroutineScope by BiliHelperPlugin.childScope("BiliListener") {


    fun subscribe(): Unit = with(globalEventChannel()) {
        subscribeMessages {
            UrlRepliers.forEach { (regex, replier) ->
                regex findingReply replier
            }
        }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}