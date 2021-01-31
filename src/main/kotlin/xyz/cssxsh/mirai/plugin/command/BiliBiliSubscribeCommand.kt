package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.verbose
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.tasks
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskInfo
import kotlin.coroutines.CoroutineContext

@Suppress("unused")
object BiliBiliSubscribeCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "bili-subscribe", "B订阅",
    description = "B站订阅指令"
), CoroutineScope {

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    override val coroutineContext: CoroutineContext = CoroutineName("Bilibili-Listener")

    private val taskJobs = mutableMapOf<Long, Job>()

    private val liveState = mutableMapOf<Long, Boolean>()

    private val taskContacts = mutableMapOf<Long, Set<Contact>>()

    private fun BilibiliTaskInfo.getContacts(bot: Bot): Set<Contact> =
        (bot.groups.filter { it.id in groups } + bot.friends.filter { it.id in friends }).toSet()

    fun onInit() = GlobalEventChannel.parentScope(BilibiliHelperPlugin).subscribeAlways<BotOnlineEvent> {
        logger.info { "开始初始化${bot}联系人列表" }
        tasks.toMap().forEach { (uid, info) ->
            taskContacts.compute(uid) { _, contacts ->
                (contacts ?: emptySet()) + info.getContacts(bot)
            }
            addListener(uid)
        }
    }

    private suspend fun sendMessageToTaskContacts(
        uid: Long, block: suspend MessageChainBuilder.(contact: Contact) -> Unit
    ) = taskContacts.getValue(uid).forEach { contact ->
        runCatching {
            contact.sendMessage(buildMessageChain {
                block(contact)
            })
        }.onFailure {
            logger.warning({ "对[${contact}]构建消息失败" }, it)
        }
    }

    private suspend fun buildVideoMessage(uid: Long) = runCatching {
        bilibiliClient.searchVideo(uid).list.vList.filter {
            it.created > tasks.getValue(uid).videoLast
        }.apply {
            logger.verbose { "(${uid})共加载${size}条视频" }
            sortedBy { it.created }.forEach { video ->
                sendMessageToTaskContacts(uid = uid) { contact ->
                    appendLine("标题: ${video.title}")
                    appendLine("作者: ${video.author}")
                    appendLine("时间: ${timestampToOffsetDateTime(video.created)}")
                    appendLine("时长: ${video.length}")
                    appendLine("链接: ${video.getVideoUrl()}")

                    runCatching {
                        add(video.getCover().uploadAsImage(contact))
                    }.onFailure {
                        logger.warning({ "获取[${video.bvId}]封面封面失败" }, it)
                        appendLine("获取[${video.bvId}]封面封面失败")
                    }
                }
            }
            maxByOrNull { it.created }?.let { video ->
                logger.info {
                    "(${uid})[${video.author}]最新视频为(${video.bvId})[${video.title}]<${
                        timestampToOffsetDateTime(video.created)
                    }>"
                }
                tasks.compute(uid) { _, info ->
                    info?.copy(videoLast = video.created)
                }
            }
        }
    }.onFailure { logger.warning({ "($uid)获取视频失败" }, it) }.isSuccess

    private suspend fun buildLiveMessage(uid: Long) = runCatching {
        bilibiliClient.getAccInfo(uid).also { user ->
            logger.verbose { "(${uid})[${user.name}][${user.liveRoom.title}]最新开播状态为${user.liveRoom.liveStatus == 1}" }
            liveState.put(uid, user.liveRoom.liveStatus == 1).let { old ->
                if (old != true && user.liveRoom.liveStatus == 1) {
                    sendMessageToTaskContacts(uid = uid) { contact ->
                        appendLine("主播: ${user.name}")
                        appendLine("标题: ${user.liveRoom.title}")
                        appendLine("人气: ${user.liveRoom.online}")
                        appendLine("链接: ${user.liveRoom.url}")

                        runCatching {
                            add(user.liveRoom.getCover().uploadAsImage(contact))
                        }.onFailure {
                            logger.warning({ "获取[${user.liveRoom.roomId}]直播间封面封面失败" }, it)
                            appendLine("获取[${user.liveRoom.roomId}]直播间封面失败")
                        }
                    }
                }
            }
        }
    }.onFailure { logger.warning({ "($uid)获取直播失败" }, it) }.isSuccess

    private suspend fun buildDynamicMessage(uid: Long) = runCatching {
        bilibiliClient.getSpaceHistory(uid).cards.filter {
            it.describe.timestamp > tasks.getValue(uid).dynamicLast
        }.apply {
            logger.verbose { "(${uid})共加载${size}条动态" }
            sortedBy { it.describe.timestamp }.forEach { dynamic ->
                logger.verbose { "(${uid})当前处理${dynamic.describe.dynamicId}" }
                sendMessageToTaskContacts(uid = uid) { contact ->
                    appendLine("${dynamic.describe.userProfile.info.uname} 有新动态")
                    appendLine("时间: ${timestampToOffsetDateTime(dynamic.describe.timestamp)}")
                    appendLine("链接: ${dynamic.getDynamicUrl()}")

                    runCatching {
                        add(dynamic.getScreenShot(refresh = false).uploadAsImage(contact))
                    }.onFailure {
                        logger.warning({ "获取动态[${dynamic.describe.dynamicId}]快照失败" }, it)
                        add(dynamic.toMessageText())
                    }

                    dynamic.getImages().forEachIndexed { index, result ->
                        runCatching {
                            add(result.getOrThrow().uploadAsImage(contact))
                        }.onFailure {
                            logger.warning({ "获取动态[${dynamic.describe.dynamicId}]图片[${index}]失败" }, it)
                            appendLine("获取动态[${dynamic.describe.dynamicId}]图片[${index}]失败")
                        }
                    }
                }
            }
            maxByOrNull { it.describe.timestamp }?.let { dynamic ->
                logger.info {
                    "(${uid})[${dynamic.describe.userProfile.info.uname}]最新动态时间为<${
                        timestampToOffsetDateTime(dynamic.describe.timestamp)
                    }>"
                }
                tasks.compute(uid) { _, info ->
                    info?.copy(dynamicLast = dynamic.describe.timestamp)
                }
            }
        }
    }.onFailure { logger.warning({ "($uid)获取动态失败" }, it) }.isSuccess

    private fun addListener(uid: Long): Job = launch {
        delay(tasks.getValue(uid).getInterval().random())
        while (isActive && taskContacts[uid].isNullOrEmpty().not()) {
            runCatching {
                buildDynamicMessage(uid)
                buildVideoMessage(uid)
                buildLiveMessage(uid)
            }.onSuccess {
                delay(tasks.getValue(uid).getInterval().random().also {
                    logger.info { "(${uid}): ${tasks[uid]}监听任务完成一次, 即将进入延时delay(${it}ms)。" }
                })
            }.onFailure {
                logger.warning({ "(${uid})监听任务执行失败" }, it)
                delay(tasks.getValue(uid).maxIntervalMillis)
            }
        }
    }.also { logger.info { "添加对(${uid})的监听任务, 添加完成${it}" } }

    private fun MutableMap<Long, Set<Contact>>.addUid(uid: Long, subject: Contact) = compute(uid) { _, list ->
        (list ?: emptySet()) + subject.also { contact ->
            tasks.compute(uid) { _, info ->
                (info ?: BilibiliTaskInfo()).run {
                    when (contact) {
                        is Friend -> copy(friends = friends + contact.id)
                        is Group -> copy(groups = groups + contact.id)
                        else -> this
                    }
                }
            }
        }
    }

    private fun MutableMap<Long, Set<Contact>>.removeUid(uid: Long, subject: Contact) = compute(uid) { _, list ->
        (list ?: emptySet()) - subject.also { contact ->
            tasks.compute(uid) { _, info ->
                info?.run {
                    when (contact) {
                        is Friend -> copy(friends = friends - contact.id)
                        is Group -> copy(groups = groups - contact.id)
                        else -> this
                    }
                }
            }
        }
    }

    @SubCommand("add", "添加")
    suspend fun CommandSenderOnMessage<MessageEvent>.add(uid: Long) = runCatching {
        taskContacts.addUid(uid, fromEvent.subject)
        taskJobs.compute(uid) { _, job ->
            job?.takeIf { it.isActive } ?: addListener(uid)
        }
    }.onSuccess { job ->
        sendMessage(fromEvent.message.quote() + "对${uid}的监听任务, 添加完成${job}")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<MessageEvent>.stop(uid: Long) = runCatching {
        taskContacts.removeUid(uid, fromEvent.subject)
        taskJobs.compute(uid) { _, job ->
            job?.takeIf { taskContacts[uid].isNullOrEmpty() }
        }
    }.onSuccess { job ->
        sendMessage(fromEvent.message.quote() + "对${uid}的监听任务, 取消完成${job}")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess

    @SubCommand("list", "列表")
    suspend fun CommandSenderOnMessage<MessageEvent>.list() = runCatching {
        buildString {
            appendLine("监听状态:")
            tasks.toMap().forEach { (uid, _) ->
                if (taskContacts.getOrDefault(uid, emptySet()).isNotEmpty()) {
                    appendLine("$uid -> ${taskJobs.getValue(uid)}")
                }
            }
        }
    }.onSuccess { text ->
        sendMessage(fromEvent.message.quote() + text)
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess
}