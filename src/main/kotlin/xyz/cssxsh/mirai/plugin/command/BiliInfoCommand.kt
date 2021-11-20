package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.plugin.*

object BiliInfoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
) {

    @SubCommand
    suspend fun UserCommandSender.aid(id: Long) = sendMessage(
        client.getVideoInfo(id).toMessage(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.bvid(id: String) = sendMessage(
        client.getVideoInfo(id).toMessage(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.dynamic(id: Long) = sendMessage(
        client.getDynamicInfo(id).dynamic.toMessage(subject)
    )

    @SubCommand
    suspend fun UserCommandSender.live(id: Long) = sendMessage(
        client.getRoomInfo(id).toMessage(subject)
    )
}