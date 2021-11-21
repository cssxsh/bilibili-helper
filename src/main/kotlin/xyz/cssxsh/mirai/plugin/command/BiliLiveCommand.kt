package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.plugin.*

object BiliLiveCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-live", "B直播",
    description = "B站直播指令"
), BiliHelperCommand, BiliTasker by BiliLiveWaiter {

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(uid: Long, contact: Contact = subject()) = sendMessage(
        message = task(uid, contact).let { "对@${it.name}#${uid}的监听任务, 添加完成" }
    )

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(uid: Long, contact: Contact = subject()) = sendMessage(
        message = remove(uid, contact).let { "对@${it.name}#${uid}的监听任务, 取消完成" }
    )

    @SubCommand("list", "列表")
    suspend fun CommandSender.detail(contact: Contact = subject()) = sendMessage(
        message = list(contact)
    )
}