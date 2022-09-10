package xyz.cssxsh.mirai.bilibili.command

import com.cronutils.model.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.bilibili.*
import xyz.cssxsh.mirai.bilibili.data.*
import java.time.*

object BiliSeasonCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-season", "B剧集", "B番剧",
    description = "B站剧集指令",
    overrideContext = BiliCommandArgumentContext
), BiliTasker by BiliSeasonWaiter {

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(sid: Long, contact: Contact = subject()) = sendMessage(
        message = task(sid, contact)
    )

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(sid: Long, contact: Contact = subject()) = sendMessage(
        message = remove(sid, contact)
    )

    @SubCommand("time", "定时")
    suspend fun CommandSender.cron(uid: Long, cron: Cron) = sendMessage(
        message = time(uid, cron)
    )

    @SubCommand("list", "列表")
    suspend fun CommandSender.detail(contact: Contact = subject()) = sendMessage(
        message = list(contact)
    )

    @SubCommand("sleep", "睡眠")
    suspend fun CommandSender.sleep(target: PermitteeId, start: LocalTime, end: LocalTime) {
        val interval = BiliInterval(start, end)
        if (interval.isEmpty()) {
            sleep.remove(target)
            sendMessage("睡眠时间取消成功")
        } else {
            sleep[target] = BiliInterval(start, end)
            sendMessage("睡眠时间添加成功")
        }
    }

    @SubCommand("at", "艾特")
    suspend fun CommandSender.at(target: PermitteeId, start: LocalTime, end: LocalTime) {
        val interval = BiliInterval(start, end)
        if (interval.isEmpty()) {
            at.remove(target)
            sendMessage("艾特时间取消成功")
        } else {
            at[target] = BiliInterval(start, end)
            sendMessage("艾特时间添加成功")
        }
    }
}