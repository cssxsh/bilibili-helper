package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.event.*
import xyz.cssxsh.mirai.plugin.command.*

@OptIn(ConsoleExperimentalApi::class)
internal object BiliListener : CoroutineScope by BiliHelperPlugin.childScope("BiliListener") {

    fun subscribe(): Unit = with(globalEventChannel()) {
        subscribeMessages {
            always {
                if (BiliInfoCommand.permission.testPermission(toCommandSender()).not()) return@always
                for ((regex, replier) in UrlRepliers) {
                    // regex findingReply replier
                    replier.invoke(this, regex.find(it) ?: continue)
                }
            }
        }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}