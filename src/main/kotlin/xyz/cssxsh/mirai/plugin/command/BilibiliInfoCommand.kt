package xyz.cssxsh.mirai.plugin.command

import io.ktor.http.*
import kotlinx.coroutines.Job
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger

object BilibiliInfoCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "bili-info", "B信息",
    description = "B站信息指令"
) {

    private val VIDEO_REGEX = """((av|AV)\d+|(bv|BV)[0-9A-z]{10})""".toRegex()

    private val DYNAMIC_REGEX = """(?<=t\.bilibili\.com/(h5/dynamic/detail/)?)([0-9]{18})""".toRegex()

    private val ROOM_REGEX = """(?<=live\.bilibili\.com/)(\d+)""".toRegex()

    private val SHORT_LINK_REGEX = """b23\.tv/[0-9A-z]+""".toRegex()

    private lateinit var subscribeJob: Job

    private val dynamicReplier: suspend MessageEvent.(MatchResult) -> Any? = { result ->
        logger.info { "[${sender}] 匹配DYNAMIC(${result.value})" }
        runCatching {
            bilibiliClient.getDynamicDetail(
                dynamicId = result.value.toLong()
            ).card.buildDynamicMessage(contact = subject, quote = message.quote())
        }.onFailure {
            logger.warning({ "构建DYNAMIC(${result.value})信息失败" }, it)
        }.getOrElse {
            it.message
        }
    }

    private val videoReplier: suspend MessageEvent.(MatchResult) -> Any? = { result ->
        logger.info { "[${sender}] 匹配VIDEO(${result.value})" }
        runCatching {
            when (result.value.first()) {
                'B', 'b' -> {
                    result.value.let {
                        bilibiliClient.getVideoInfo(bvid = it)
                    }
                }
                'A', 'a' -> {
                    result.value.substring(2).toLong().let {
                        bilibiliClient.getVideoInfo(aid = it)
                    }
                }
                else -> throw IllegalArgumentException("未知视频ID(${result.value})")
            }.buildVideoMessage(contact = subject, quote = message.quote())
        }.onFailure {
            logger.warning({ "构建VIDEO(${result.value})信息失败" }, it)
        }.getOrElse {
            it.message
        }
    }

    private val roomReplier: suspend MessageEvent.(MatchResult) -> Any? = { result ->
        logger.info { "[${sender}] 匹配ROOM(${result.value})" }
        runCatching {
            bilibiliClient.getRoomInfo(
                roomId = result.value.toLong()
            ).buildRoomMessage(contact = subject, quote = message.quote())
        }.onFailure {
            logger.warning({ "构建ROOM(${result.value})信息失败" }, it)
        }.getOrElse {
            it.message
        }
    }

    fun start() {
        subscribeJob = GlobalEventChannel.parentScope(BilibiliHelperPlugin).subscribeMessages {
            DYNAMIC_REGEX findingReply dynamicReplier
            VIDEO_REGEX findingReply videoReplier
            ROOM_REGEX findingReply roomReplier
            SHORT_LINK_REGEX findingReply { result ->
                logger.info { "[${sender}] 匹配SHORT_LINK(${result.value}) 尝试跳转" }
                Url(result.value).getLocation()?.let { url ->
                    DYNAMIC_REGEX.find(url)?.let { result ->
                        dynamicReplier(result)
                    } ?: VIDEO_REGEX.find(url)?.let { result ->
                        videoReplier(result)
                    } ?: ROOM_REGEX.find(url)?.let { result ->
                        roomReplier(result)
                    }
                }
            }
        }
    }

    fun stop() {
        subscribeJob.cancel()
    }

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    private suspend fun BiliVideoInfo.buildVideoMessage(contact: Contact, quote: QuoteReply? = null) = buildMessageChain {
        quote?.let { add(it) }
        appendLine("标题: $title")
        appendLine("作者: ${owner.name}")
        appendLine("时间: ${timestampToOffsetDateTime(pubdate)}")
        appendLine("时长: ${durationText()}")
        appendLine("链接: ${getVideoUrl()}")

        runCatching {
            add(getCover().uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取[${title}](${bvid})}视频封面失败" }, it)
        }
    }

    private suspend fun BiliCardInfo.buildDynamicMessage(contact: Contact, quote: QuoteReply? = null) = buildMessageChain {
        quote?.let { add(it) }
        appendLine("${describe.userProfile.info.uname} 动态")
        appendLine("时间: ${timestampToOffsetDateTime(describe.timestamp)}")
        appendLine("链接: ${getDynamicUrl()}")

        runCatching {
            add(getScreenShot(refresh = true).uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取动态${describe.dynamicId}快照失败" }, it)
            add(toMessageText())
        }

        getImages().forEachIndexed { index, result ->
            runCatching {
                add(result.getOrThrow().uploadAsImage(contact))
            }.onFailure {
                logger.warning({ "获取动态${describe.dynamicId}图片[${index}]失败" }, it)
                appendLine("获取动态${describe.dynamicId}图片[${index}]失败")
            }
        }
    }

    private suspend fun BiliRoomInfo.buildRoomMessage(contact: Contact, quote: QuoteReply? = null) = buildMessageChain {
        quote?.let { add(it) }
        when(liveStatus) {
            0 -> {
                appendLine("未开播")
                runCatching {
                    bilibiliClient.getOffLiveList(roomId = roomId, count = 1).run {
                        appendLine(tips)
                        records.firstOrNull()?.run {
                            appendLine("直播回放: ${getRecordUrl()}")
                            appendLine("主播: $uname")
                            appendLine("标题: $title")
                            appendLine("时间: $startTime")

                            runCatching {
                                add(getCover().uploadAsImage(contact))
                            }.onFailure {
                                logger.warning({ "获取[${rid}]直播回放封面封面失败" }, it)
                                appendLine("获取[${rid}]直播回放封面失败")
                            }
                        }
                    }
                }
            }
            1 -> {
                appendLine("开播时间: ${timestampToOffsetDateTime(liveTime)}")
                runCatching {
                    bilibiliClient.getAccInfo(uid = uid).run {
                        appendLine("主播: $name")
                        appendLine("标题: ${liveRoom.title}")
                        appendLine("人气: ${liveRoom.online}")

                        runCatching {
                            add(liveRoom.getCover().uploadAsImage(contact))
                        }.onFailure {
                            logger.warning({ "获取[${uid}]直播间封面封面失败" }, it)
                            appendLine("获取[${uid}]直播间封面失败")
                        }
                    }
                }.onFailure {
                    logger.warning({ "获取[${uid}]直播间信息失败" }, it)
                    appendLine("获取[${uid}]直播间信息失败")
                }
            }
            2 -> {
                appendLine("轮播中")
                runCatching {
                    bilibiliClient.getRoundPlayVideo(roomId = roomId).run {
                        appendLine("标题: $title")
                        appendLine("链接: $bvidUrl")
                    }
                }.onFailure {
                    logger.warning({ "获取[${roomId}]轮播信息失败" }, it)
                    appendLine("获取[${roomId}]轮播信息失败")
                }
            }
        }
    }

    @SubCommand
    suspend fun CommandSenderOnMessage<MessageEvent>.aid(id: Long) = runCatching {
        bilibiliClient.getVideoInfo(aid = id).buildVideoMessage(
            contact = fromEvent.subject,
            quote = fromEvent.message.quote()
        ).let {
            sendMessage(it)
        }
    }.onFailure { sendMessage(it.toString()) }.isSuccess

    @SubCommand
    suspend fun CommandSenderOnMessage<MessageEvent>.bvid(id: String) = runCatching {
        bilibiliClient.getVideoInfo(bvid = id).buildVideoMessage(
            contact = fromEvent.subject,
            quote = fromEvent.message.quote()
        ).let {
            sendMessage(it)
        }
    }.onFailure { sendMessage(it.toString()) }.isSuccess

    @SubCommand
    suspend fun CommandSenderOnMessage<MessageEvent>.dynamic(id: Long) = runCatching {
        bilibiliClient.getDynamicDetail(id).card.buildDynamicMessage(
            contact = fromEvent.subject,
            quote = fromEvent.message.quote()
        ).let {
            sendMessage(it)
        }
    }.onFailure { sendMessage(it.toString()) }.isSuccess

    @SubCommand
    suspend fun  CommandSenderOnMessage<MessageEvent>.live(id: Long) = runCatching {
        bilibiliClient.getRoomInfo(id).buildRoomMessage(
            contact = fromEvent.subject,
            quote = fromEvent.message.quote()
        ).let {
            sendMessage(it)
        }
    }.onFailure { sendMessage(it.toString()) }.isSuccess
}