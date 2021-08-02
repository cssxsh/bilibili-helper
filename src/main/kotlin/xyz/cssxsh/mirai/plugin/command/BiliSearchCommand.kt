package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.Contact
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.plugin.*

object BiliSearchCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-search", "B搜索",
    description = "B站搜索指令"
) {

    @SubCommand("user", "用户")
    @Description("搜索用户")
    suspend fun CommandSender.user(keyword: String, contact: Contact = subject()) = sendMessage(
        client.searchUser(keyword).toMessage(contact)
    )

    @SubCommand("bangumi", "番剧")
    @Description("搜索番剧")
    suspend fun CommandSender.bangumi(keyword: String, contact: Contact = subject()) = sendMessage(
        client.searchBangumi(keyword).toMessage(contact)
    )

    @SubCommand("ft", "影视")
    @Description("搜索影视")
    suspend fun CommandSender.ft(keyword: String, contact: Contact = subject()) = sendMessage(
        client.searchFT(keyword).toMessage(contact)
    )
}