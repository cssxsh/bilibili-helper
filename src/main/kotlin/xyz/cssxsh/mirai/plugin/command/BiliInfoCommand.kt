package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.utils.*
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

    @SubCommand
    suspend fun UserCommandSender.reg(vararg uids: String) = sendMessage(
        message = buildString {
            logger.info { "reg ${uids.toList()}" }
            val result = client.getMultiple(uids = LongArray(uids.size) { uids[it].toLong() })
            result.mapValues { it.value.card }.forEach { (_, card) ->
                appendLine("UID: ${card.uid}")
                appendLine("简介: ${card.officialVerify.description}")
                appendLine("注册时间: ${card.datetime}")
            }
        }
    )
}