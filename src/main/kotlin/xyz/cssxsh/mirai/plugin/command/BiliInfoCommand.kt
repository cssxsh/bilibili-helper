package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.plugin.*

object BiliInfoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
), BiliHelperCommand {

    @SubCommand
    suspend fun UserCommandSender.aid(id: Long) = sendMessage(
        message = client.getVideoInfo(id).content(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.bvid(id: String) = sendMessage(
        message = client.getVideoInfo(id).content(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.dynamic(id: Long) = sendMessage(
        message = client.getDynamicInfo(id).dynamic.content(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.live(id: Long) = sendMessage(
        message = client.getRoomInfo(id).content(subject)
    )
}