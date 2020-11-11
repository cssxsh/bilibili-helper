package mirai.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.MessageEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.tasks

object StateCommand : SimpleCommand(
    owner = TempCommandOwner,
    "state", "状态",
    description = "状态指令"
) {

    private val startTime = LocalDateTime.now()

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    @ConsoleExperimentalApi
    @Handler
    suspend fun CommandSenderOnMessage<MessageEvent>.handle() {
        quoteReply(buildString {
            appendLine("启动时间: ${startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}")
            appendLine("订阅人数: ${tasks.size}")
        })
    }
}