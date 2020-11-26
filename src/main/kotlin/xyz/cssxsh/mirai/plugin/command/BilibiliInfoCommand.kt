package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.serialization.json.Json
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
import xyz.cssxsh.bilibili.api.getDynamicDetail
import xyz.cssxsh.bilibili.api.videoInfo
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.bilibili.data.BiliVideoInfo.VideoData
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.DYNAMIC_DETAIL
import xyz.cssxsh.mirai.plugin.getBilibiliImage
import xyz.cssxsh.mirai.plugin.getScreenShot
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

object BilibiliInfoCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
    }

    private val VIDEO_REGEX = """(?=https://(?=m.)?bilibili.com/video/)?(av\d+|BV[0-9A-z]{10})""".toRegex()

    private val DYNAMIC_REGEX = """(?=https://t.bilibili.com/(?=h5/dynamic/detail/)?)?\d+""".toRegex()

    fun CoroutineScope.subscribeBilibiliInfo(): Job = subscribeMessages {
        finding(VIDEO_REGEX) { result ->
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
                }.videoData.buildVideoMessage(subject)
            }.onFailure {
                logger.warning({ "构建VIDEO(${result.value})信息失败" }, it)
            }
        }
        finding(DYNAMIC_REGEX) { result ->
            runCatching {
                bilibiliClient.getDynamicDetail(result.value.toLong()).dynamic
            }.onFailure {
                logger.warning({ "构建DYNAMIC(${result.value})信息失败" }, it)
            }
        }
    }

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    private fun timestampToFormatText(timestamp: Long): String =
        Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).format(ISO_OFFSET_DATE_TIME)

    private fun Int.durationText() =
        "${this / 60}:${this % 60}"

    private suspend fun VideoData.buildVideoMessage(contact: Contact) = buildMessageChain {
        appendLine("标题: ${title}")
        appendLine("作者: ${owner.name}")
        appendLine("时间: ${timestampToFormatText(pubDate)}")
        appendLine("时长: ${duration.durationText()}")
        appendLine("链接: https://www.bilibili.com/video/${bvId}")

        runCatching {
            getBilibiliImage(pic, "${bvId}-cover")
        }.onSuccess {
            add(it.uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取[${title}](${bvId})}视频封面失败" }, it)
        }
    }.let { contact.sendMessage(it) }

    private suspend fun BiliCardInfo.buildDynamicMessage(contact: Contact) = buildMessageChain {
        appendLine("${desc.userProfile.info.uname} 有新动态")
        appendLine("时间: ${timestampToFormatText(desc.timestamp)}")
        appendLine("链接: https://t.bilibili.com/${desc.dynamicId}")

        runCatching {
            getScreenShot(url = DYNAMIC_DETAIL + desc.dynamicId, name = "${desc.dynamicId}")
        }.onSuccess {
            add(it.uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取动态${desc.dynamicId}快照失败" }, it)
            when (desc.type) {
                1 -> {
                    json.decodeFromJsonElement(BiliReplyCard.serializer(), card).let { card ->
                        add("${card.user.uname} -> ${card.originUser.info.uname}: \n${card.item.content}")
                    }
                }
                2 -> {
                    json.decodeFromJsonElement(BiliPictureCard.serializer(), card).let { card ->
                        add("${card.user.name}: \n${card.item.description}")
                    }
                }
                4 -> {
                    json.decodeFromJsonElement(BiliTextCard.serializer(), card).let { card ->
                        add("${card.user.uname}: \n${card.item.content}")
                    }
                }
                8 -> {
                    json.decodeFromJsonElement(BiliVideoCard.serializer(), card).let { card ->
                        add("${card.owner.name}: \n${card.title}")
                    }
                }
                else -> {
                }
            }
        }

        if (desc.type == BiliPictureCard.TYPE) {
            json.decodeFromJsonElement(
                BiliPictureCard.serializer(),
                card
            ).item.pictures.forEachIndexed { index, picture ->
                runCatching {
                    getBilibiliImage(picture.imgSrc, "${desc.dynamicId}-${index}.jpg")
                }.onSuccess {
                    add(it.uploadAsImage(contact))
                }.onFailure {
                    logger.warning({ "动态图片下载失败: ${picture.imgSrc}" }, it)
                }
            }
        }
    }.let { contact.sendMessage(it) }

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.aid(id: Long) = runCatching {
        bilibiliClient.videoInfo(aid = id).videoData.buildVideoMessage(fromEvent.subject)
    }.onFailure { reply(it.toString()) }.isSuccess

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.bvid(id: String) = runCatching {
        bilibiliClient.videoInfo(bvId = id).videoData.buildVideoMessage(fromEvent.subject)
    }.onFailure { reply(it.toString()) }.isSuccess

    @SubCommand
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.dynamic(id: Long) = runCatching {
        bilibiliClient.getDynamicDetail(id).dynamic.card.buildDynamicMessage(fromEvent.subject)
    }.onFailure { reply(it.toString()) }.isSuccess
}