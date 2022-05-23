package xyz.cssxsh.mirai.bilibili.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.bilibili.*
import xyz.cssxsh.mirai.bilibili.data.*

object BiliTaskCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-task", "B任务",
    description = "B站任务列表指令"
), BiliHelperCommand {

    private fun BiliTasker.render(): String = buildString {
        for ((id, info) in tasks) {
            if (info.contacts.isEmpty()) continue
            appendLine("@${info.name}#$id -> ${info.last}")
            for (delegate in info.contacts) {
                appendLine("    ${findContact(delegate)?.render() ?: delegate}")
            }
        }
    }

    private fun BiliTaskerConfig.render(): String = buildString {
        appendLine("屏蔽视频分区: $videoForbidType")
        appendLine("屏蔽付费视频: $videoForbidPay")
        appendLine("屏蔽联合视频: $videoForbidUnion")
        appendLine("屏蔽回放视频: $videoForbidInteract")
        appendLine("屏蔽动态类型: $dynamicForbidType")
        appendLine("屏蔽动态正则: $dynamicForbidRegexes")
    }

    @SubCommand
    suspend fun CommandSender.all() = sendMessage(message = buildMessageChain {
        for (tasker in BiliTasker) {
            if (tasker.tasks.all { (_, task) -> task.contacts.isEmpty() }) continue
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

    @SubCommand
    suspend fun CommandSender.forbid() = sendMessage(message = BiliTaskerConfig.render())
}