package xyz.cssxsh.mirai.plugin.command

import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
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
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.verbose
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.bilibili.api.accInfo
import xyz.cssxsh.bilibili.api.dynamicInfo
import xyz.cssxsh.bilibili.api.searchVideo
import xyz.cssxsh.bilibili.data.BiliPictureCard
import xyz.cssxsh.bilibili.data.BiliReplyCard
import xyz.cssxsh.bilibili.data.BiliTextCard
import xyz.cssxsh.bilibili.data.BiliVideoCard
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.bilibiliClient
import xyz.cssxsh.mirai.plugin.BilibiliHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.chromePath
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.deviceName
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.driverUrl
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.tasks
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.timeoutMillis
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskInfo
import xyz.cssxsh.mirai.plugin.tools.BilibiliChromeDriverTool
import xyz.cssxsh.mirai.plugin.tools.getScreenShot
import java.net.URL
import java.time.Instant.ofEpochSecond
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import kotlin.coroutines.CoroutineContext

object BiliBiliSubscribeCommand : CompositeCommand(
    owner = BilibiliHelperPlugin,
    "subscribe", "订阅",
    description = "B站订阅指令"
), CoroutineScope {

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    private const val DYNAMIC_DETAIL = "https://t.bilibili.com/h5/dynamic/detail/"

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
    }

    override val coroutineContext: CoroutineContext = CoroutineName("Bilibili-Listener")

    private val taskJobs = mutableMapOf<Long, Job>()

    private val liveState = mutableMapOf<Long, Boolean>()

    private val taskContacts = mutableMapOf<Long, Set<Contact>>()

    private fun timestampToFormatText(timestamp: Long): String =
        ofEpochSecond(timestamp).atZone(ZoneOffset.systemDefault()).format(ISO_OFFSET_DATE_TIME)

    private fun BilibiliTaskInfo.getContacts(bot: Bot): Set<Contact> =
        (bot.groups.filter { it.id in groups } + bot.friends.filter { it.id in friends }).toSet()

    fun onInit() = BilibiliHelperPlugin.subscribeAlways<BotOnlineEvent> {
        logger.info { "开始初始化${bot}联系人列表" }
        tasks.toMap().forEach { (uid, info) ->
            taskContacts.compute(uid) { _, contacts ->
                (contacts ?: emptySet()) + info.getContacts(bot)
            }
            addListener(uid)
        }
    }

    private suspend fun List<Any>.sendMessageToTaskContacts(uid: Long) = taskContacts.getValue(uid).forEach { contact ->
        contact.runCatching {
            sendMessage(map {
                when (it) {
                    is String -> PlainText(it)
                    is Message -> it
                    is ByteArray -> it.inputStream().uploadAsImage(contact)
                    else -> PlainText(it.toString())
                }
            }.asMessageChain())
        }
    }

    private suspend fun getScreenShot(url: String): ByteArray = runCatching {
        BilibiliChromeDriverTool(URL(driverUrl), chromePath, deviceName).useDriver {
            it.getScreenShot(url, timeoutMillis)
        }
    }.onFailure {
        logger.warning({ "使用ChromeDriver(${driverUrl})失败" }, it)
    }.getOrElse {
        bilibiliClient.useHttpClient {
            it.get("https://www.screenshotmaster.com/api/screenshot") {
                parameter("url", url)
                parameter("width", 768)
                parameter("height", 1024)
                parameter("zone", "gz")
                parameter("device", "table")
                parameter("delay", 500)
            }
        }
    }

    private suspend fun buildVideoMessage(uid: Long) = runCatching {
        bilibiliClient.searchVideo(uid).searchData.list.vList.apply {
            filter {
                it.created > tasks.getValue(uid).videoLast
            }.forEach { video ->
                buildList<Any> {
                    add(buildString {
                        appendLine("标题: ${video.title}")
                        appendLine("作者: ${video.author}")
                        appendLine("时间: ${timestampToFormatText(video.created)}")
                        appendLine("时长: ${video.length}")
                        appendLine("链接: https://www.bilibili.com/video/${video.bvId}")
                    })
                    runCatching {
                        bilibiliClient.useHttpClient<ByteArray> {
                            it.get(video.pic)
                        }
                    }.onSuccess {
                        add(it)
                    }.onFailure {
                        logger.warning({ "获取[${video.title}](${video.bvId})}视频封面失败" }, it)
                    }
                }.sendMessageToTaskContacts(uid)
            }
            maxByOrNull { it.created }?.let { video ->
                logger.verbose {
                    "(${uid})[${video.author}]最新视频为(${video.bvId})[${video.title}]<${
                        timestampToFormatText(video.created)
                    }>"
                }
                tasks.compute(uid) { _, info ->
                    info?.copy(videoLast = video.created)
                }
            }
        }
    }.onFailure { logger.warning({ "($uid)获取视频失败" }, it) }.isSuccess

    private suspend fun buildLiveMessage(uid: Long) = runCatching {
        bilibiliClient.accInfo(uid).userData.also { user ->
            logger.verbose { "(${uid})[${user.name}][${user.liveRoom.title}]最新开播状态为${user.liveRoom.liveStatus == 1}" }
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
                            bilibiliClient.useHttpClient<ByteArray> {
                                it.get(user.liveRoom.cover)
                            }
                        }.onSuccess {
                            add(it)
                        }.onFailure {
                            logger.warning({ "获取[${uid}]直播间封面封面失败" }, it)
                        }
                    }.sendMessageToTaskContacts(uid)
                }
            }
        }
    }.onFailure { logger.warning({ "($uid)获取直播失败" }, it) }.isSuccess

    private suspend fun buildDynamicMessage(uid: Long) = runCatching {
        bilibiliClient.dynamicInfo(uid).dynamicData.cards.apply {
            filter {
                it.desc.timestamp > tasks.getValue(uid).dynamicLast
            }.forEach { dynamic ->
                buildList<Any> {
                    add(buildString {
                        appendLine("${dynamic.desc.userProfile.info.uname} 有新动态")
                        appendLine("时间: ${timestampToFormatText(dynamic.desc.timestamp)}")
                        appendLine("链接: https://t.bilibili.com/${dynamic.desc.dynamicId}")
                    })
                    runCatching {
                        add(getScreenShot(url = DYNAMIC_DETAIL + dynamic.desc.dynamicId))
                    }.onFailure {
                        logger.warning({ "获取动态${dynamic.desc.dynamicId}快照失败" }, it)
                        when (dynamic.desc.type) {
                            1 -> {
                                json.decodeFromJsonElement(BiliReplyCard.serializer(), dynamic.card).let { card ->
                                    add("${card.user.uname} -> ${card.originUser.info.uname}: \n${card.item.content}")
                                }
                            }
                            2 -> {
                                json.decodeFromJsonElement(BiliPictureCard.serializer(), dynamic.card).let { card ->
                                    add("${card.user.name}: \n${card.item.description}")
                                }
                            }
                            4 -> {
                                json.decodeFromJsonElement(BiliTextCard.serializer(), dynamic.card).let { card ->
                                    add("${card.user.uname}: \n${card.item.content}")
                                }
                            }
                            8 -> {
                                json.decodeFromJsonElement(BiliVideoCard.serializer(), dynamic.card).let { card ->
                                    add("${card.owner.name}: \n${card.title}")
                                }
                            }
                            else -> {
                            }
                        }
                    }
                    if (dynamic.desc.type == 2) {
                        json.decodeFromJsonElement(
                            BiliPictureCard.serializer(),
                            dynamic.card
                        ).item.pictures.forEach { picture ->
                            runCatching {
                                bilibiliClient.useHttpClient<ByteArray> {
                                    it.get(picture.imgSrc)
                                }
                            }.onSuccess {
                                add(it)
                            }.onFailure {
                                logger.warning({ "动态图片下载失败: ${picture.imgSrc}" }, it)
                            }
                        }
                    }
                }.sendMessageToTaskContacts(uid)
            }
            maxByOrNull { it.desc.timestamp }?.let { dynamic ->
                logger.verbose {
                    "(${uid})[${dynamic.desc.userProfile.info.uname}]最新动态时间为<${
                        timestampToFormatText(dynamic.desc.timestamp)
                    }>"
                }
                tasks.compute(uid) { _, info ->
                    info?.copy(dynamicLast = dynamic.desc.timestamp)
                }
            }
        }
    }.onFailure { logger.warning({ "($uid)获取动态失败" }, it) }.isSuccess

    private fun BilibiliTaskInfo.getInterval() = minIntervalMillis..maxIntervalMillis

    private fun addListener(uid: Long): Job = launch {
        delay(tasks.getValue(uid).getInterval().random())
        while (isActive && taskContacts[uid].isNullOrEmpty().not()) {
            runCatching {
                buildVideoMessage(uid) && buildLiveMessage(uid) && buildDynamicMessage(uid)
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
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.add(uid: Long) = runCatching {
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
            job?.takeIf { taskContacts[uid].isNullOrEmpty() }
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
            tasks.toMap().forEach { (uid, _) ->
                if (taskContacts.getOrDefault(uid, emptySet()).isNotEmpty()) {
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