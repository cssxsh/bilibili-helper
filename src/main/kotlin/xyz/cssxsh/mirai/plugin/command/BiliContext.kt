package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.descriptor.*
import net.mamoe.mirai.contact.*

fun CommandSender.subject(): Contact = subject ?: throw CommandArgumentParserException("无法从当前环境获取联系人")