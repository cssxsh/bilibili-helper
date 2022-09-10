package xyz.cssxsh.mirai.bilibili.command

import com.cronutils.model.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.bilibili.*
import xyz.cssxsh.mirai.bilibili.data.*
import java.time.*

object BiliVideoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-video", "B视频",
    description = "B站视频指令",
    overrideContext = BiliCommandArgumentContext
), BiliTasker by BiliVideoLoader {

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(uid: Long, contact: Contact = subject()) = sendMessage(
        message = task(uid, contact)
    )

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(uid: Long, contact: Contact = subject()) = sendMessage(
        message = remove(uid, contact)
    )

    @SubCommand("time", "定时")
    suspend fun CommandSender.cron(uid: Long, cron: Cron) = sendMessage(
        message = time(uid, cron)
    )

    @SubCommand("list", "列表")
    suspend fun CommandSender.detail(contact: Contact = subject()) = sendMessage(
        message = list(contact)
    )

    @SubCommand("forbid", "屏蔽")
    suspend fun CommandSender.forbid(type: String, open: Boolean = true) {
        when (type) {
            "付费" -> BiliTaskerConfig.videoForbidPay = open
            "联合" -> BiliTaskerConfig.videoForbidUnion = open
            "回放" -> BiliTaskerConfig.videoForbidInteract = open
            else -> {
                sendMessage(message = "莫得这个选项")
                return
            }
        }

        sendMessage(message = type + if (open) " 屏蔽开启" else " 屏蔽关闭")
    }

    @SubCommand("filter", "过滤")
    @Description("请参考 https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/video/video_zone.md")
    suspend fun CommandSender.filter(tid: Int, add: Boolean = true) {
        if (add) {
            BiliTaskerConfig.dynamicForbidType.add(tid)
            sendMessage(message = "视频分区<$tid>屏蔽添加成功")
        } else {
            BiliTaskerConfig.dynamicForbidType.remove(tid)
            sendMessage(message = "视频分区<$tid>屏蔽取消成功")
        }
    }

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