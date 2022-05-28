package xyz.cssxsh.mirai.bilibili.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.bilibili.*
import xyz.cssxsh.mirai.bilibili.data.*
import java.time.format.*

object BiliTemplateCommand : CompositeCommand(
    owner = BiliHelperPlugin,
    "bili-template", "B模板",
    description = "B站模板配置指令"
), BiliHelperCommand {

    @SubCommand
    suspend fun CommandSender.datetime(pattern: String) {
        val message = try {
            BiliTemplate.formatter(pattern = pattern)
            BiliTaskerConfig.pattern = pattern
            System.setProperty(BiliTemplate.DATE_TIME_PATTERN, pattern)
            "时间日期格式设置成功"
        } catch (cause: Throwable) {
            cause.message ?: cause.toString()
        }
        sendMessage(message = message)
    }

    @SubCommand
    suspend fun CommandSender.reload() {
        val message = try {
            BiliTemplate.reload()
            "模板加载成功"
        } catch (cause: Throwable) {
            cause.message ?: cause.toString()
        }
        sendMessage(message = message)
    }
}