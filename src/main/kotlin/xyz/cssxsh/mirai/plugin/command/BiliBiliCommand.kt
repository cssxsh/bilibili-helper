package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.*
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.maxIntervalMillis
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.minIntervalMillis
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.sendImage
import kotlin.coroutines.CoroutineContext

@ConsoleExperimentalApi
object BiliBiliCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "bilibili", "B站",
    description = "缓存指令",
    prefixOptional = true
), CoroutineScope {

    private val logger get() = BilibiliHelperPlugin.logger

    override val coroutineContext: CoroutineContext = CoroutineName("Bilibili-Listener")

    private val taskJobs = mutableMapOf<Long, Job>()

    private val liveState = mutableMapOf<Long, Boolean>()

    private val taskContacts = mutableMapOf<Long, Set<Contact>>()

    private val intervalMillis = minIntervalMillis..maxIntervalMillis

    private fun BilibiliTaskData.TaskInfo.getContacts(bot: Bot): Set<Contact> =
        (bot.groups.filter { it.id in groups } + bot.friends.filter { it.id in friends }).toSet()

    fun onInit() = BilibiliHelperPlugin.subscribeAlways<BotOnlineEvent> {
        logger.info("开始初始化${bot}联系人列表")
        BilibiliTaskData.tasks.toMap().forEach { (uid, info) ->
            taskContacts[uid] = info.getContacts(bot).also {
                if (it.isNotEmpty()) addListener(uid)
            }
        }
    }

    private suspend fun buildVideoMessage(uid: Long) {
        runCatching {
            BilibiliHelperPlugin.searchVideo(uid).searchData.list.vList.filter {
                it.created >= BilibiliTaskData.tasks.getOrPut(uid) { BilibiliTaskData.TaskInfo() }.videoLast
            }.apply {
                maxByOrNull { it.created }?.let { video ->
                    logger.verbose("(${uid})[${video.author}]>最新视频为[${video.title}](${video.bvId})<${video.created}>")
                    BilibiliTaskData.tasks.compute(uid) { _, info ->
                        info?.copy(videoLast = video.created)
                    }
                }
                forEach { video ->
                    buildString {
                        appendLine("标题: ${video.title}")
                        appendLine("作者: ${video.author}")
                        appendLine("时长: ${video.length}")
                        appendLine("链接: https://www.bilibili.com/video/${video.bvId}")
                    }.let { info ->
                        taskContacts.getValue(uid).forEach { contact ->
                            contact.runCatching {
                                sendMessage(info)
                                sendImage(BilibiliHelperPlugin.getPic(video.pic).inputStream())
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun buildLiveMessage(uid: Long) {
        runCatching {
            BilibiliHelperPlugin.accInfo(uid).userData.also { user ->
                logger.verbose("(${uid})[${user.name}][${user.liveRoom.title}]最新开播状态为${user.liveRoom.liveStatus == 1}")
                liveState.put(uid, user.liveRoom.liveStatus == 1).let {
                    if (it != true && user.liveRoom.liveStatus == 1) {
                        buildString {
                            appendLine("主播: ${user.name}")
                            appendLine("标题: ${user.liveRoom.title}")
                            appendLine("人气: ${user.liveRoom.online}")
                            appendLine("链接: ${user.liveRoom.url}")
                        }.let { info ->
                            taskContacts.getValue(uid).forEach { contact ->
                                contact.runCatching {
                                    sendMessage(info)
                                    sendImage(BilibiliHelperPlugin.getPic(user.liveRoom.cover).inputStream())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun buildDynamicMessage(uid: Long) {
        runCatching {
            BilibiliHelperPlugin.dynamicInfo(uid).dynamicData.cards.map {
                it.card
            }.filter {
                it.item.uploadTime >= BilibiliTaskData.tasks.getOrPut(uid) { BilibiliTaskData.TaskInfo() }.dynamicLast
            }.apply {
                maxByOrNull { it.item.uploadTime }?.let { dynamic ->
                    logger.verbose("(${uid})[${dynamic.user.name}]最新动态为[${dynamic.item.description}]<${dynamic.item.uploadTime}>")
                    BilibiliTaskData.tasks.compute(uid) { _, info ->
                        info?.copy(dynamicLast = dynamic.item.uploadTime)
                    }
                }
                forEach { dynamic ->
                    buildString {
                        appendLine("作者: ${dynamic.user.name}")
                        appendLine("内容: ${dynamic.item.description}")
                    }.let { info ->
                        taskContacts.getValue(uid).forEach { contact ->
                            contact.runCatching {
                                sendMessage(info)
                                dynamic.item.pictures.forEach {
                                    sendImage(BilibiliHelperPlugin.getPic(it.imgSrc).inputStream())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addListener(uid: Long): Job = launch {
        liveState[uid] = false
        while (isActive) {
            runCatching {
                buildVideoMessage(uid)
                buildLiveMessage(uid)
                buildDynamicMessage(uid)
            }.onSuccess {
                (intervalMillis.random()).let {
                    logger.verbose("(${uid})监听任务完成一次, 即将进入延时delay(${it}ms)。")
                    delay(it)
                }
            }.onFailure {
                logger.warning("(${uid})监听任务执行失败", it)
                delay(minIntervalMillis)
            }
            runCatching {

            }
        }
    }.also { logger.verbose("添加对${uid}的监听任务, 添加完成${it}") }

    private fun MutableMap<Long, Set<Contact>>.addUid(uid: Long, subject: Contact) = compute(uid) { _, list ->
        (list ?: emptySet()) + subject.also { contact ->
            BilibiliTaskData.tasks.compute(uid) { _, info ->
                (info ?: BilibiliTaskData.TaskInfo()).run {
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
        (list ?: emptySet()) - subject.also {
            BilibiliTaskData.tasks.remove(uid)
        }
    }

    @SubCommand("task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.task(uid: Long) = runCatching {
        taskContacts.addUid(uid, fromEvent.subject)
        taskJobs.compute(uid) { _, job ->
            job?.takeIf { it.isActive } ?: addListener(uid)
        }
    }.onSuccess { job ->
        quoteReply("添加对${uid}的监听任务, 添加完成${job}")
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.stop(uid: Long) = runCatching {
        taskContacts.removeUid(uid, fromEvent.subject)
        taskJobs.compute(uid) { _, job ->
            if (taskContacts[uid].isNullOrEmpty()) {
                job?.cancel()
                BilibiliTaskData.tasks.remove(uid)
                null
            } else {
                job
            }
        }
    }.onSuccess { job ->
        quoteReply("添加对${uid}的监听任务, 添加完成${job}")
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess

    @SubCommand("list", "列表")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.list() = runCatching {
        buildString {
            appendLine("监听状态:")
            BilibiliTaskData.tasks.toMap().forEach { (uid, info) ->
                if (fromEvent.subject.id in info.groups + info.friends) {
                    appendLine("$uid -> ${taskJobs.getValue(uid)}")
                }
            }
        }
    }.onSuccess { text ->
        quoteReply(text)
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess
}