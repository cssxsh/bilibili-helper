package xyz.cssxsh.mirai.plugin.command

import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.uploadAsImage
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.bilibili.api.videoInfo
import xyz.cssxsh.bilibili.data.BiliVideoInfo.VideoData
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger
import java.time.Instant.ofEpochSecond
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object BilibiliInfoCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
) {

    fun CoroutineScope.subscribeBilibiliInfo(): Job = subscribeMessages {
        matching("""(av\d+|BV[0-9A-z]{10})""".toRegex()) { result ->
            runCatching {
                when (result.value.first()) {
                    'B' -> {
                        result.value.let {
                            bilibiliClient.videoInfo(bvId = it)
                        }
                    }
                    'a' -> {
                        result.value.substring(2).toLong().let {
                            bilibiliClient.videoInfo(aid = it)
                        }
                    }
                    else -> throw IllegalArgumentException("未知视频ID")
                }.videoData.buildVideoMessage(subject).let {
                    reply(it)
                }
            }.onFailure {
                logger.warning({ "构建${result.value}信息失败" }, it)
            }
        }
    }

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    private fun timestampToFormatText(timestamp: Long): String =
        ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private fun Int.durationText() =
        "${this / 60}:${this % 60}"

    private suspend fun VideoData.buildVideoMessage(contact: Contact) = buildMessageChain {
        appendLine("标题: ${title}")
        appendLine("作者: ${owner.name}")
        appendLine("时间: ${timestampToFormatText(pubDate)}")
        appendLine("时长: ${duration.durationText()}")
        appendLine("链接: https://www.bilibili.com/video/${bvId}")
        runCatching {
            bilibiliClient.useHttpClient<ByteArray> {
                it.get(pic)
            }
        }.onSuccess {
            add(it.inputStream().uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取[${title}](${bvId})}视频封面失败" }, it)
        }
    }

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.aid(id: Long) = runCatching {
        reply(bilibiliClient.videoInfo(aid = id).videoData.buildVideoMessage(fromEvent.subject))
    }.onFailure { reply(it.toString()) }.isSuccess

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.bvid(id: String) = runCatching {
        reply(bilibiliClient.videoInfo(bvId = id).videoData.buildVideoMessage(fromEvent.subject))
    }.onFailure { reply(it.toString()) }.isSuccess
}