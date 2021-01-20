package mirai.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildMessageChain
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.tasks
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

object StateCommand : SimpleCommand(
    owner = TempCommandOwner,
    "helper-state", "助手状态",
    description = "状态指令"
) {

    private val startTime = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0)

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<MessageEvent>.handle() {
        sendMessage(buildMessageChain {
            add(fromEvent.message.quote())
            appendLine("启动时间: ${startTime.format(ISO_OFFSET_DATE_TIME)}")
            appendLine("订阅人数: ${tasks.size}")
        })
    }
}