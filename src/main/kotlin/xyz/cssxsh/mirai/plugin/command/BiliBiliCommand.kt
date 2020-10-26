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

    private val videoJobs = mutableMapOf<Long, Job>()

    private val videoContact = mutableMapOf<Long, Set<Contact>>()

    private val liveJobs = mutableMapOf<Long, Job>()

    private val liveState = mutableMapOf<Long, Boolean>()

    private val liveContact = mutableMapOf<Long, Set<Contact>>()

    private val intervalMillis = minIntervalMillis..maxIntervalMillis

    private fun BilibiliTaskData.TaskInfo.getContacts(): Set<Contact> = Bot.botInstances.flatMap { bot ->
        bot.groups.filter { it.id in groups } + bot.friends.filter { it.id in friends }
    }.toSet()

    fun onInit() {
        BilibiliTaskData.video.toMap().forEach { (uid, info) ->
            videoContact[uid] = info.getContacts()
            addVideoListener(uid)
        }
        BilibiliTaskData.live.toMap().forEach { (uid, info) ->
            liveContact[uid] = info.getContacts()
            addLiveListener(uid)
        }
    }

    private fun addVideoListener(uid: Long): Job = launch {
        while (isActive) {
            runCatching {
                BilibiliHelperPlugin.searchVideo(uid).searchData.list.vList.apply {
                    maxByOrNull { it.created }?.let { video ->
                        logger.verbose("(${uid})最新视频为[${video.title}](${video.bvId})<${video.created}>")
                    }
                }.filter {
                    it.created >= BilibiliTaskData.video.getOrPut(uid) { BilibiliTaskData.TaskInfo() }.last
                }.apply {
                    maxByOrNull { it.created }?.let { video ->
                        BilibiliTaskData.video.compute(uid) { _, info ->
                            info?.copy(last = video.created)
                        }
                    }
                    forEach { video ->
                        buildString {
                            appendLine("标题: ${video.title}")
                            appendLine("作者: ${video.author}")
                            appendLine("时长: ${video.length}")
                            appendLine("链接: https://www.bilibili.com/video/${video.bvId}")
                        }.let { info ->
                            videoContact.getValue(uid).forEach { contact ->
                                contact.runCatching {
                                    sendMessage(info)
                                    sendImage(BilibiliHelperPlugin.getPic(video.pic).inputStream())
                                }
                            }
                        }
                    }
                }
            }.onSuccess { list ->
                (intervalMillis.random()).let {
                    logger.verbose("(${uid})[${videoContact.getValue(uid)}]视频监听任务完成一次, 目前时间戳为${BilibiliTaskData.video.getValue(uid).last}, 共有${list.size}个视频更新, 即将进入延时delay(${it}ms)。")
                    delay(it)
                }
            }.onFailure {
                logger.warning("(${uid})视频监听任务执行失败", it)
                delay(minIntervalMillis)
            }
        }
    }.also { logger.verbose("添加对${uid}的视频监听任务, 添加完成${it}") }

    private fun addLiveListener(uid: Long): Job = launch {
        liveState[uid] = false
        while (isActive) {
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
                                liveContact.getValue(uid).forEach { contact ->
                                    contact.runCatching {
                                        sendMessage(info)
                                        sendImage(BilibiliHelperPlugin.getPic(user.liveRoom.cover).inputStream())
                                    }
                                }
                            }
                        }
                    }
                }
            }.onSuccess { user ->
                (intervalMillis.random()).let {
                    logger.verbose("(${uid})[${user.name}][${liveContact.getValue(uid)}]直播监听任务完成一次, 目前开播状态为${user.liveRoom.liveStatus}, 即将进入延时delay(${it}ms)。")
                    delay(it)
                }
            }.onFailure {
                logger.warning("(${uid})直播监听任务执行失败", it)
                delay(minIntervalMillis)
            }
        }
    }.also { logger.verbose("添加对${uid}的直播监听任务, 添加完成${it}") }

    @SubCommand("video", "视频")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.video(uid: Long) = runCatching {
        videoContact.compute(uid) { _, list ->
            (list ?: emptySet()) + fromEvent.subject.also { contact ->
                BilibiliTaskData.video.compute(uid) { _, info ->
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
        videoJobs.compute(uid) { _, job ->
            job?.takeIf { it.isActive } ?: addVideoListener(uid)
        }
    }.onSuccess { job ->
        quoteReply("添加对${uid}的视频监听任务, 添加完成${job}")
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess

    @SubCommand("live", "直播")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.live(uid: Long) = runCatching {
        liveContact.compute(uid) { _, list ->
            (list ?: emptySet()) + fromEvent.subject.also { contact ->
                BilibiliTaskData.live.compute(uid) { _, info ->
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
        liveJobs.compute(uid) { _, job ->
            job?.takeIf { it.isActive } ?: addLiveListener(uid)
        }
    }.onSuccess { job ->
        quoteReply("添加对${uid}的直播监听任务, 添加完成${job}")
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess

    @SubCommand("list", "列表")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.list() = runCatching {
        buildString {
            appendLine("视频监听:")
            BilibiliTaskData.video.toMap().forEach { (uid, info) ->
                if (fromEvent.subject.id in info.groups + info.friends) appendLine(uid)
            }
            appendLine("直播监听:")
            BilibiliTaskData.live.toMap().forEach { (uid, info) ->
                if (fromEvent.subject.id in info.groups + info.friends) appendLine(uid)
            }
        }
    }.onSuccess { text ->
        quoteReply(text)
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess
}