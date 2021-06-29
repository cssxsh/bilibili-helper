package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.Contact
import xyz.cssxsh.mirai.plugin.*

object BiliVideoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-video", "B视频",
    description = "B站视频指令"
), BiliTasker by BiliVideoLoader {

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(uid: Long, contact: Contact = Contact()) = sendMessage(
        addContact(uid, contact).let { "对@${it?.name}#${uid}的监听任务, 添加完成" }
    )

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(uid: Long, contact: Contact = Contact()) = sendMessage(
        removeContact(uid, contact).let { "对@${it?.name}#${uid}的监听任务, 取消完成" }
    )

    @SubCommand("list", "列表")
    suspend fun CommandSenderOnMessage<*>.detail(contact: Contact = Contact()) = sendMessage(
        list(contact)
    )
}