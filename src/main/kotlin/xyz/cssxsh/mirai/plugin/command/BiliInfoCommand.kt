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
    suspend fun CommandSenderOnMessage<*>.aid(id: Long) = sendMessage(
        client.getVideoInfo(id).toMessage(subject!!)
    )

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.bvid(id: String) = sendMessage(
        client.getVideoInfo(id).toMessage(subject!!)
    )

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.dynamic(id: Long) = sendMessage(
        client.getDynamicInfo(id).dynamic.toMessage(subject!!)
    )

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.live(id: Long) = sendMessage(
        client.getRoomInfo(id).toMessage(subject!!)
    )
}