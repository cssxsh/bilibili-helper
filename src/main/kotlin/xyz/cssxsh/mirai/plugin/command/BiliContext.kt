package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.descriptor.CommandArgumentParserException
import net.mamoe.mirai.contact.Contact

fun CommandSender.subject(): Contact = subject ?: throw CommandArgumentParserException("无法从当前环境获取联系人")