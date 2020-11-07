package xyz.cssxsh.mirai.plugin.command

import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.asMessageChain
import net.mamoe.mirai.message.uploadAsImage
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.bilibili.api.accInfo
import xyz.cssxsh.bilibili.api.dynamicInfo
import xyz.cssxsh.bilibili.api.searchVideo
import xyz.cssxsh.bilibili.data.BiliPicCard
import xyz.cssxsh.bilibili.data.BiliReplyCard
import xyz.cssxsh.bilibili.data.BiliTextCard
import kotlin.coroutines.CoroutineContext

object BiliBiliCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "bilibili", "B站",
    description = "缓存指令"
), CoroutineScope {

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    private const val DYNAMIC_DETAIL = "https://t.bilibili.com/h5/dynamic/detail/"

    private const val SCREENSHOT = "https://www.screenshotmaster.com/api/screenshot"

    private val logger get() = BilibiliHelperPlugin.logger

    private val bilibiliClient = BilibiliClient(emptyMap())

    override val coroutineContext: CoroutineContext = CoroutineName("Bilibili-Listener")

    private val taskJobs = mutableMapOf<Long, Job>()

    private val liveState = mutableMapOf<Long, Boolean>()

    private val taskContacts = mutableMapOf<Long, Set<Contact>>()

    private fun BilibiliTaskData.TaskInfo.getContacts(bot: Bot): Set<Contact> =
        (bot.groups.filter { it.id in groups } + bot.friends.filter { it.id in friends }).toSet()

    fun onInit() = BilibiliHelperPlugin.subscribeAlways<BotOnlineEvent> {
        logger.info("开始初始化${bot}联系人列表")
        BilibiliTaskData.tasks.toMap().forEach { (uid, info) ->
            taskContacts[uid] = info.getContacts(bot)
            addListener(uid)
        }
    }

    private suspend fun List<Any>.sendMessageToTaskContacts(uid: Long) = taskContacts.getValue(uid).forEach { contact ->
        contact.runCatching {
            sendMessage(map {
                when(it) {
                    is String -> PlainText(it)
                    is Message -> it
                    is ByteArray -> it.inputStream().uploadAsImage(contact)
                    else -> PlainText(it.toString())
                }
            }.asMessageChain())
        }
    }

    private suspend fun buildVideoMessage(uid: Long) = runCatching {
        bilibiliClient.searchVideo(uid).searchData.list.vList.apply {
            filter {
                it.created > BilibiliTaskData.tasks.getOrPut(uid) { BilibiliTaskData.TaskInfo() }.videoLast
            }.forEach { video ->
                buildList<Any> {
                    add(buildString {
                        appendLine("标题: ${video.title}")
                        appendLine("作者: ${video.author}")
                        appendLine("时长: ${video.length}")
                        appendLine("链接: https://www.bilibili.com/video/${video.bvId}")
                    })
                    runCatching {
                        add(bilibiliClient.useHttpClient<ByteArray> {
                            it.get(video.pic)
                        })
                    }.onFailure {
                        logger.warning("获取[${video.title}](${video.bvId})}视频封面失败", it)
                    }
                }.sendMessageToTaskContacts(uid)
            }
            maxByOrNull { it.created }?.let { video ->
                logger.verbose("(${uid})[${video.author}]>最新视频为[${video.title}](${video.bvId})<${video.created}>")
                BilibiliTaskData.tasks.compute(uid) { _, info ->
                    info?.copy(videoLast = video.created)
                }
            }
        }
    }.onFailure { logger.warning("($uid)获取视频失败", it) }.isSuccess

    private suspend fun buildLiveMessage(uid: Long) = runCatching {
        bilibiliClient.accInfo(uid).userData.also { user ->
            logger.verbose("(${uid})[${user.name}][${user.liveRoom.title}]最新开播状态为${user.liveRoom.liveStatus == 1}")
            liveState.put(uid, user.liveRoom.liveStatus == 1).let { old ->
                if (old != true && user.liveRoom.liveStatus == 1) {
                    buildList<Any> {
                        add(buildString {
                            appendLine("主播: ${user.name}")
                            appendLine("标题: ${user.liveRoom.title}")
                            appendLine("人气: ${user.liveRoom.online}")
                            appendLine("链接: ${user.liveRoom.url}")
                        })
                        runCatching {
                            add(bilibiliClient.useHttpClient<ByteArray> {
                                it.get(user.liveRoom.cover)
                            })
                        }.onFailure {
                            logger.warning("获取[${uid}]直播间封面封面失败", it)
                        }
                    }.sendMessageToTaskContacts(uid)
                }
            }
        }
    }.onFailure { logger.warning("($uid)获取直播失败", it) }.isSuccess

    private suspend fun buildDynamicMessage(uid: Long) = runCatching {
        bilibiliClient.dynamicInfo(uid).dynamicData.cards.apply {
            filter {
                it.desc.timestamp > BilibiliTaskData.tasks.getOrPut(uid) { BilibiliTaskData.TaskInfo() }.dynamicLast
            }.forEach { dynamic ->
                buildList<Any> {
                    add(buildString {
                        appendLine("${dynamic.desc.userProfile.info.uname} 有新动态")
                        appendLine("链接: https://t.bilibili.com/${dynamic.desc.dynamicId}")
                    })
                    runCatching {
                        add(bilibiliClient.useHttpClient<ByteArray> {
                            it.get(SCREENSHOT) {
                                parameter("url", DYNAMIC_DETAIL + dynamic.desc.dynamicId)
                                parameter("width", 768)
                                parameter("height", 1024)
                                parameter("zone", "gz")
                                parameter("device", "table")
                                parameter("delay", 500)
                            }
                        })
                    }.onFailure {
                        logger.warning("获取动态${dynamic.desc.dynamicId}快照失败", it)
                        when(dynamic.desc.type) {
                            1 -> buildList {
                                Json.decodeFromJsonElement(BiliReplyCard.serializer(), dynamic.card).let { card ->
                                    add("${card.user.uname} -> ${card.originUser.info.uname}: \n${card.item.content}")
                                }
                            }
                            2 -> buildList {
                                Json.decodeFromJsonElement(BiliPicCard.serializer(), dynamic.card).let { card ->
                                    add("${card.user.name}: \n${card.item.description}")
                                }
                            }
                            4 -> buildList {
                                Json.decodeFromJsonElement(BiliTextCard.serializer(), dynamic.card).let { card ->
                                    add("${card.user.uname}: \n${card.item. content}")
                                }
                            }
                            else -> {}
                        }
                    }
                    if (dynamic.desc.type == 2) {
                        addAll(Json.decodeFromJsonElement(BiliPicCard.serializer(), dynamic.card).item.pictures.mapNotNull { picture ->
                            runCatching {
                                bilibiliClient.useHttpClient<ByteArray> {
                                    it.get(picture.imgSrc)
                                }
                            }.getOrNull()
                        })
                    }
                }.sendMessageToTaskContacts(uid)
            }
            maxByOrNull { it.desc.timestamp }?.let { dynamic ->
                logger.verbose("(${uid})[${dynamic.desc.userProfile.info.uname}]最新动态时间为<${dynamic.desc.timestamp}>")
                BilibiliTaskData.tasks.compute(uid) { _, info ->
                    info?.copy(dynamicLast = dynamic.desc.timestamp)
                }
            }
        }
    }.onFailure { logger.warning("($uid)获取动态失败", it) }.isSuccess

    private fun addListener(uid: Long): Job = launch {
        val intervalMillis = BilibiliTaskData.tasks.getValue(uid).run {
            minIntervalMillis..maxIntervalMillis
        }
        while (isActive && taskContacts[uid].isNullOrEmpty().not()) {
            runCatching {
                buildVideoMessage(uid)
                buildLiveMessage(uid)
                buildDynamicMessage(uid)
            }.onSuccess {
                delay(intervalMillis.random().also {
                    logger.info("(${uid}): ${BilibiliTaskData.tasks[uid]}监听任务完成一次, 即将进入延时delay(${it}ms)。")
                })
            }.onFailure {
                logger.warning("(${uid})监听任务执行失败", it)
                delay(intervalMillis.last)
            }
        }
    }.also { logger.info("添加对${uid}的监听任务, 添加完成${it}") }

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
        quoteReply("对${uid}的监听任务, 添加完成${job}")
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.stop(uid: Long) = runCatching {
        taskContacts.removeUid(uid, fromEvent.subject)
        taskJobs.compute(uid) { _, job ->
            if (taskContacts[uid].isNullOrEmpty()) {
                BilibiliTaskData.tasks.remove(uid)
                null
            } else {
                job
            }
        }
    }.onSuccess { job ->
        quoteReply("对${uid}的监听任务, 取消完成${job}")
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