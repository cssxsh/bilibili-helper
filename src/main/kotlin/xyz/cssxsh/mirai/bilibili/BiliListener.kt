package xyz.cssxsh.mirai.bilibili

import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.bilibili.command.*
import xyz.cssxsh.mirai.bilibili.data.*
import kotlin.coroutines.*
import kotlin.coroutines.cancellation.*

internal object BiliListener : SimpleListenerHost() {
    private val permission get() = BiliInfoCommand.permission
    private val ban get() = BiliHelperSettings.ban
    private val forward get() = BiliHelperSettings.forward

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        when (exception) {
            is ExceptionInEventHandlerException -> logger.warning({ "BiliListener Handle Exception" }, exception.cause)
            is CancellationException -> Unit
            else -> logger.warning({ "BiliListener Exception" }, exception)
        }
    }

    @EventHandler
    suspend fun MessageEvent.handle() {
        // XXX: MessageSyncEvent permission
        if (this !is MessageSyncEvent && permission.testPermission(toCommandSender()).not()) return

        for ((regex, replier) in UrlRepliers) {
            val result = regex.find(message.contentToString()) ?: continue
            if (ban.any { it.equals(other = result.value, ignoreCase = true) }) continue
            val message = replier(result) ?: continue

            // XXX: 转发模式 https://github.com/cssxsh/bilibili-helper/issues/90
            if (forward) {
                subject.sendMessage(message.toForwardMessage(sender = sender, time = time))
            } else {
                subject.sendMessage(message)
            }
        }
    }
}