package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.Contact
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.plugin.*

object BiliInfoCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
) {

    @SubCommand
    suspend fun CommandSender.aid(id: Long, contact: Contact = Contact()) = contact.sendMessage(
        client.getVideoInfo(aid = id).toMessage(contact = contact)
    )

    @SubCommand
    suspend fun CommandSender.bvid(id: String, contact: Contact = Contact()) = contact.sendMessage(
        client.getVideoInfo(bvid = id).toMessage(contact = contact)
    )

    @SubCommand
    suspend fun CommandSender.dynamic(id: Long, contact: Contact = Contact()) = contact.sendMessage(
        client.getDynamicInfo(id).dynamic.toMessage(contact = contact)
    )

    @SubCommand
    suspend fun CommandSender.live(id: Long, contact: Contact = Contact()) = contact.sendMessage(
        client.getRoomInfo(id).toMessage(contact = contact)
    )
}