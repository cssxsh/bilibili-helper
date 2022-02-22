package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import kotlin.coroutines.*
import kotlin.coroutines.cancellation.*

internal object BiliListener : SimpleListenerHost() {
    private val permission get() = BiliInfoCommand.permission
    private val ban get() = BiliHelperSettings.ban

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        when (exception) {
            is ExceptionInEventHandlerException -> logger.warning({ "BiliListener Handle Exception" }, exception.cause)
            is CancellationException -> Unit
            else -> logger.warning({ "BiliListener Exception" }, exception)
        }
    }

    @EventHandler
    suspend fun MessageEvent.handle() {
        if (this is MessageSyncEvent) return
        if (permission.testPermission(toCommandSender()).not()) return

        for ((regex, replier) in UrlRepliers) {
            val result = regex.find(message.contentToString()) ?: continue
            if (ban.any { it.equals(other = result.value, ignoreCase = true) }) continue

            when (val message = replier(result)) {
                is Message -> subject.sendMessage(message)
                null, is Unit -> Unit
                else -> subject.sendMessage(message.toString())
            }
        }
    }
}