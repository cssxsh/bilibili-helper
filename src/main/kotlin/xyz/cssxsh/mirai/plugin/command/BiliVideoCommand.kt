package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*

object BiliVideoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-video", "B视频",
    description = "B站视频指令"
), BiliTasker by BiliVideoLoader {

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

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
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage { list(fromEvent.subject).toPlainText()  }
}