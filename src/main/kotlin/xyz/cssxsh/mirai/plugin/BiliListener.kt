package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import xyz.cssxsh.mirai.plugin.command.BiliInfoCommand

@OptIn(ConsoleExperimentalApi::class)
internal object BiliListener : CoroutineScope by BiliHelperPlugin.childScope("BiliListener") {

    fun subscribe(): Unit = with(globalEventChannel()) {
        subscribeMessages {
            always {
                if (BiliInfoCommand.permission.testPermission(sender.permitteeId).not()) return@always
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