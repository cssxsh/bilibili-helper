package xyz.cssxsh.mirai.bilibili.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.bilibili.*
import xyz.cssxsh.mirai.bilibili.data.*

object BiliVideoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-video", "B视频",
    description = "B站视频指令"
), BiliHelperCommand, BiliTasker by BiliVideoLoader {

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(uid: Long, contact: Contact = subject()) = sendMessage(
        message = task(uid, contact)
    )

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(uid: Long, contact: Contact = subject()) = sendMessage(
        message = remove(uid, contact)
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
}