package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*

object BiliSeasonCommand: CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-season", "B剧集", "B番剧",
    description = "B站剧集指令"
), BiliTasker by BiliSeasonWaiter {

    @SubCommand("add", "添加")
    suspend fun CommandSenderOnMessage<*>.add(id: Long) = sendMessage {
        addContact(id, fromEvent.subject).let {
            "对@${it?.name}#${id}的监听任务, 添加完成".toPlainText()
        }
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<*>.stop(id: Long) = sendMessage {
       removeContact(id, fromEvent.subject).let {
            "对@${it?.name}#${id}的监听任务, 取消完成".toPlainText()
        }
    }

    @SubCommand("list", "列表")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage { list(fromEvent.subject).toPlainText() }
}