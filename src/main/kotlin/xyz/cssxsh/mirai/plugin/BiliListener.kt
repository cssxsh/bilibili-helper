package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import xyz.cssxsh.bilibili.SHORT_LINK_REGEX

internal object BiliListener: CoroutineScope by BiliHelperPlugin.childScope("BiliListener") {

    fun subscribe() {
        globalEventChannel().subscribeMessages {
            Repliers.forEach { (regex, replier) ->
                regex findingReply replier
            }
        }
    }

    fun stop()  {
        coroutineContext.cancelChildren()
    }
}