package xyz.cssxsh.mirai.plugin.command

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
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.verbose
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.bilibili.api.getDynamicDetail
import xyz.cssxsh.bilibili.api.videoInfo
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.data.BiliVideoInfo.VideoData
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger

object BilibiliInfoCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
) {

    private val VIDEO_REGEX = """(?<=https://(m|www)\.bilibili\.com/video/)?(av\d+|BV[0-9A-z]{10})""".toRegex()

    private val DYNAMIC_REGEX = """(?<=https://t\.bilibili\.com/(h5/dynamic/detail/)?)([0-9]{18})""".toRegex()

    fun CoroutineScope.subscribeBilibiliInfo(): Job = subscribeMessages {
        finding(DYNAMIC_REGEX) { result ->
            logger.info { "匹配DYNAMIC(${result.value})" }
            runCatching {
                bilibiliClient.getDynamicDetail(result.value.toLong()).dynamic.card.buildDynamicMessage(subject).let {
                    quoteReply(it)
                }
            }.onFailure {
                logger.warning({ "构建DYNAMIC(${result.value})信息失败" }, it)
            }
        }
        finding(VIDEO_REGEX) { result ->
            logger.info { "匹配VIDEO(${result.value})" }
            runCatching {
                when (result.value.first()) {
                    'B', 'b' -> {
                        result.value.let {
                            bilibiliClient.videoInfo(bvId = it)
                        }
                    }
                    'A', 'a' -> {
                        result.value.substring(2).toLong().let {
                            bilibiliClient.videoInfo(aid = it)
                        }
                    }
                    else -> throw IllegalArgumentException("未知视频ID(${result.value})")
                }.videoData.buildVideoMessage(subject).let {
                    quoteReply(it)
                }
            }.onFailure {
                logger.warning({ "构建VIDEO(${result.value})信息失败" }, it)
            }
        }
    }

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    private fun VideoData.durationText() = "${duration / 3600}:${duration % 3600 / 60}:${duration % 60}"

    private suspend fun VideoData.buildVideoMessage(contact: Contact) = buildMessageChain {
        appendLine("标题: $title")
        appendLine("作者: ${owner.name}")
        appendLine("时间: ${timestampToFormatText(pubDate)}")
        appendLine("时长: ${durationText()}")
        appendLine("链接: https://www.bilibili.com/video/${bvId}")

        runCatching {
            getBilibiliImage(
                url = pic,
                name ="video-${bvId}-cover",
                refresh = true
            )
        }.onSuccess {
            add(it.uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取[${title}](${bvId})}视频封面失败" }, it)
        }
    }

    private suspend fun BiliCardInfo.buildDynamicMessage(contact: Contact) = buildMessageChain {
        appendLine("${desc.userProfile.info.uname} 有新动态")
        appendLine("时间: ${timestampToFormatText(desc.timestamp)}")
        appendLine("链接: https://t.bilibili.com/${desc.dynamicId}")

        runCatching {
            add(getScreenShot(refresh = true).uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取动态${desc.dynamicId}快照失败" }, it)
            add(toMessageText())
        }

        getImages().forEach {
            add(it.uploadAsImage(contact))
        }
    }

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.aid(id: Long) = runCatching {
        bilibiliClient.videoInfo(aid = id).videoData.buildVideoMessage(fromEvent.subject).let {
            quoteReply(it)
        }
    }.onFailure { reply(it.toString()) }.isSuccess

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.bvid(id: String) = runCatching {
        bilibiliClient.videoInfo(bvId = id).videoData.buildVideoMessage(fromEvent.subject).let {
            quoteReply(it)
        }
    }.onFailure { reply(it.toString()) }.isSuccess

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.dynamic(id: Long) = runCatching {
        bilibiliClient.getDynamicDetail(id).dynamic.card.buildDynamicMessage(fromEvent.subject).let {
            quoteReply(it)
        }
    }.onFailure { reply(it.toString()) }.isSuccess
}