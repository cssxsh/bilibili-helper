package xyz.cssxsh.mirai.bilibili.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.bilibili.*

object BiliInfoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
), BiliHelperCommand {

    @SubCommand
    suspend fun UserCommandSender.aid(id: Long) = sendMessage(
        message = client.getVideoInfo(aid = id).content(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.bvid(id: String) = sendMessage(
        message = client.getVideoInfo(bvid = id).content(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.dynamic(id: Long) = sendMessage(
        message = client.getDynamicInfo(dynamicId = id).dynamic.content(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.live(id: Long) = sendMessage(
        message = client.getLiveInfo(roomId = id).content(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.user(id: Long) = sendMessage(
        message = client.getUserInfo(uid = id).content(subject)
    )
}