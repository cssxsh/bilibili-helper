package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Contact
import xyz.cssxsh.mirai.plugin.*

object BiliSeasonCommand: CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-season", "B剧集", "B番剧",
    description = "B站剧集指令"
), BiliTasker by BiliSeasonWaiter {

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(sid: Long, contact: Contact = subject()) = sendMessage(
        addContact(sid, contact).let { "对@${it?.name}#${sid}的监听任务, 添加完成" }
    )

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(sid: Long, contact: Contact = subject()) = sendMessage(
        removeContact(sid, contact).let { "对@${it?.name}#${sid}的监听任务, 取消完成" }
    )

    @SubCommand("list", "列表")
    suspend fun CommandSender.detail(contact: Contact = subject()) = sendMessage(
        list(contact)
    )
}