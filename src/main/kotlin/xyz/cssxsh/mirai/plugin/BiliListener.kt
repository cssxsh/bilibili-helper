package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import xyz.cssxsh.mirai.plugin.command.*

internal object BiliListener : CoroutineScope by BiliHelperPlugin.childScope("BiliListener") {
    private val permission by BiliInfoCommand::permission

    fun subscribe() {
        globalEventChannel()
            .filter { it is MessageEvent && it !is MessageSyncEvent && permission.testPermission(it.toCommandSender()) }
            .subscribeMessages {
                for ((regex, replier) in UrlRepliers) {
                    regex findingReply replier
                }
            }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}