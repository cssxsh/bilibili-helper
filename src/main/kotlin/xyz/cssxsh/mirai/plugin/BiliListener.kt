package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.event.*
import xyz.cssxsh.mirai.plugin.command.*

internal object BiliListener : CoroutineScope by BiliHelperPlugin.childScope("BiliListener") {
    private val permission by BiliInfoCommand::permission

    fun subscribe(): Unit = with(globalEventChannel()) {
        subscribeMessages {
            always {
                if (permission.testPermission(toCommandSender()).not()) return@always
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