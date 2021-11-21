package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.plugin.*

object BiliTaskCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-task", "B任务",
    description = "B站任务列表指令"
), BiliHelperCommand {

    private fun BiliTasker.render(): String = buildString {
        for ((id, info) in tasks) {
            appendLine("@${info.name}#$id -> ${info.last}")
            for (delegate in info.contacts) {
                appendLine("    ${findContact(delegate)?.render() ?: delegate}")
            }
        }
    }

    @SubCommand
    suspend fun CommandSender.all() = sendMessage(message = buildMessageChain {
        for (tasker in BiliTasker) {
            appendLine(tasker::class.simpleName)
            appendLine(tasker.render())
        }
    })

    @SubCommand
    suspend fun CommandSender.dynamic() = sendMessage(message = BiliDynamicLoader.render())

    @SubCommand
    suspend fun CommandSender.live() = sendMessage(message = BiliLiveWaiter.render())

    @SubCommand
    suspend fun CommandSender.season() = sendMessage(message = BiliSeasonWaiter.render())

    @SubCommand
    suspend fun CommandSender.video() = sendMessage(message = BiliVideoLoader.render())
}