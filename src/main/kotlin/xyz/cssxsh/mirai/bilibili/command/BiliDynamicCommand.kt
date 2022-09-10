package xyz.cssxsh.mirai.bilibili.command

import com.cronutils.model.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.mirai.bilibili.*
import xyz.cssxsh.mirai.bilibili.data.*
import java.time.*

object BiliDynamicCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-dynamic", "B动态",
    description = "B站动态指令",
    overrideContext = BiliCommandArgumentContext
), BiliTasker by BiliDynamicLoader {

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
    suspend fun CommandSender.forbid(pattern: String, add: Boolean = true) {
        val message = if (add) {
            try {
                pattern.toRegex()
                BiliTaskerConfig.dynamicForbidRegexes.add(pattern)
                "动态正则屏蔽添加成功"
            } catch (cause: Exception) {
                logger.warning(cause)
                "动态正则屏蔽添加失败，${cause.message}"
            }
        } else {
            BiliTaskerConfig.dynamicForbidRegexes.remove(pattern)
            "动态正则屏蔽取消成功"
        }

        sendMessage(message = message)
    }

    @SubCommand("filter", "过滤")
    suspend fun CommandSender.filter(type: String, add: Boolean = true) {
        val tid = when (type) {
            "回复" -> DynamicType.REPLY
            "图片" -> DynamicType.PICTURE
            "文本" -> DynamicType.TEXT
            "视频" -> DynamicType.VIDEO
            "专栏" -> DynamicType.ARTICLE
            "音乐" -> DynamicType.MUSIC
            "剧集" -> DynamicType.EPISODE
            "删除" -> DynamicType.DELETE
            "番剧" -> DynamicType.BANGUMI
            "电视" -> DynamicType.TV
            "直播" -> DynamicType.LIVE
            else -> {
                sendMessage(message = "莫得这个选项")
                return
            }
        }

        if (add) {
            BiliTaskerConfig.dynamicForbidType.add(tid)
            sendMessage(message = "动态类型<$tid>屏蔽添加成功")
        } else {
            BiliTaskerConfig.dynamicForbidType.remove(tid)
            sendMessage(message = "动态类型<$tid>屏蔽取消成功")
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