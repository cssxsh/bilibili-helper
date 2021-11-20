package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.plugin.*

object BiliSearchCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-search", "B搜索",
    description = "B站搜索指令"
) {

    @SubCommand("user", "用户")
    @Description("搜索用户")
    suspend fun UserCommandSender.user(keyword: String) = sendMessage(
        client.searchUser(keyword).toMessage(subject)
    )

    @SubCommand("bangumi", "番剧")
    @Description("搜索番剧")
    suspend fun UserCommandSender.bangumi(keyword: String) = sendMessage(
        client.searchBangumi(keyword).toMessage(subject)
    )

    @SubCommand("ft", "影视")
    @Description("搜索影视")
    suspend fun UserCommandSender.ft(keyword: String) = sendMessage(
        client.searchFT(keyword).toMessage(subject)
    )
}