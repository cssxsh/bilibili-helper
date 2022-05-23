package xyz.cssxsh.mirai.bilibili.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.bilibili.*

object BiliSeasonCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-season", "B剧集", "B番剧",
    description = "B站剧集指令"
), BiliHelperCommand, BiliTasker by BiliSeasonWaiter {

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(sid: Long, contact: Contact = subject()) = sendMessage(
        message = task(sid, contact)
    )

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(sid: Long, contact: Contact = subject()) = sendMessage(
        message = remove(sid, contact)
    )

    @SubCommand("list", "列表")
    suspend fun CommandSender.detail(contact: Contact = subject()) = sendMessage(
        message = list(contact)
    )
}