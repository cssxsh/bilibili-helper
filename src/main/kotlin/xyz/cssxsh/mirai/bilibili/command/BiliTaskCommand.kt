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
) {

    private fun BiliTasker.render(): String = buildString {
        for ((id, info) in tasks) {
            if (info.contacts.isEmpty()) continue
            appendLine("@${info.name}#$id -> ${info.cron?.asString() ?: info.last}")
            for (delegate in info.contacts) {
                appendLine("    ${findContact(delegate)?.render() ?: delegate}")
            }
        }
    }

    private fun BiliTaskerConfig.render(): String = buildString {
        appendLine("动态睡眠时间: ")
        for ((id, interval) in dynamicSleep) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
        appendLine("动态艾特时间: ")
        for ((id, interval) in dynamicAt) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
        appendLine("屏蔽动态类型: $dynamicForbidType")
        appendLine("屏蔽动态正则: $dynamicForbidRegexes")
        appendLine()
        appendLine("视频睡眠时间: ")
        for ((id, interval) in videoSleep) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
        appendLine("视频艾特时间: ")
        for ((id, interval) in videoAt) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
        appendLine("屏蔽视频分区: $videoForbidType")
        appendLine("屏蔽付费视频: $videoForbidPay")
        appendLine("屏蔽联合视频: $videoForbidUnion")
        appendLine("屏蔽回放视频: $videoForbidInteract")
        appendLine()
        appendLine("番剧睡眠时间: ")
        for ((id, interval) in seasonSleep) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
        appendLine("番剧艾特时间: ")
        for ((id, interval) in seasonAt) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
        appendLine()
        appendLine("直播睡眠时间: ")
        for ((id, interval) in liveSleep) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
        appendLine("直播艾特时间: ")
        for ((id, interval) in liveAt) {
            append("    ").append(id).append(" ").appendLine(interval)
        }
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
    suspend fun CommandSender.config() = sendMessage(message = BiliTaskerConfig.render())
}