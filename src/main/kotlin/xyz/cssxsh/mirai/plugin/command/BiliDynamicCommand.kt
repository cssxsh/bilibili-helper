package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*

object BiliDynamicCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-dynamic", "B动态",
    description = "B站动态指令"
), BiliTasker by BiliDynamicLoader {

    @SubCommand("add", "添加")
    suspend fun CommandSenderOnMessage<*>.add(uid: Long) = sendMessage {
        addContact(uid, fromEvent.subject).let {
            "对@${it?.name}#${uid}的监听任务, 添加完成".toPlainText()
        }
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<*>.stop(uid: Long) = sendMessage {
        removeContact(uid, fromEvent.subject).let {
            "对@${it?.name}#${uid}的监听任务, 取消完成".toPlainText()
        }
    }

    @SubCommand("list", "列表")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage { list(fromEvent.subject).toPlainText() }
}