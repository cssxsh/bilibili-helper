package xyz.cssxsh.mirai.bilibili

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.permission.PermitteeId.Companion.hasChild
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import xyz.cssxsh.mirai.bilibili.data.*
import java.time.*
import kotlin.coroutines.*
import kotlin.math.*
import kotlinx.serialization.*

interface BiliTasker {

    suspend fun task(id: Long, subject: Contact): String

    suspend fun remove(id: Long, subject: Contact): String

    suspend fun list(subject: Contact): String

    fun start()

    fun stop()

    val tasks: Map<Long, BiliTask>

    companion object : Collection<BiliTasker> {
        private val taskers: List<BiliTasker> by lazy {
            AbstractTasker::class.sealedSubclasses.flatMap { it.sealedSubclasses }.mapNotNull { it.objectInstance }
        }

        override val size: Int get() = taskers.size

        override fun contains(element: BiliTasker): Boolean = taskers.contains(element)

        override fun containsAll(elements: Collection<BiliTasker>): Boolean = taskers.containsAll(elements)

        override fun isEmpty(): Boolean = taskers.isEmpty()

        override fun iterator(): Iterator<BiliTasker> = taskers.iterator()

        fun refresh() {
            val now = OffsetDateTime.now()
            BiliTaskData.dynamic.replaceAll { _, task -> task.copy(last = now) }
            BiliTaskData.video.replaceAll { _, task -> task.copy(last = now) }
        }
    }
}

sealed class AbstractTasker<T : Entry>(val name: String) : BiliTasker, CoroutineScope {

    override val coroutineContext: CoroutineContext = try {
        BiliHelperPlugin.childScopeContext(name, Dispatchers.IO)
    } catch (_: Throwable) {
        Dispatchers.IO.childScopeContext(name)
    }

    protected val mutex = Mutex()

    protected abstract val fast: Long

    protected abstract val slow: Long

    abstract override val tasks: MutableMap<Long, BiliTask>

    protected abstract suspend fun T.build(contact: Contact): Message

    protected open fun empty(id: Long) = tasks[id]?.contacts.isNullOrEmpty()

    protected open fun BiliTask.send(item: T) = contacts.map { delegate ->
        async {
            try {
                val contact = requireNotNull(findContact(delegate)) { "找不到联系人 $delegate" }
                val message = item.build(contact)
                if (message != EmptyMessageChain) {
                    contact.sendMessage(message)
                } else {
                    null
                }
            } catch (e: Throwable) {
                logger.warning({ "对[${delegate}]构建消息失败" }, e)
                null
            }
        }
    }

    private val jobs: MutableMap<Long, Job> = HashMap()

    protected abstract suspend fun listen(id: Long): Long

    protected open fun addListener(id: Long) = launch(SupervisorJob()) {
        while (isActive && !empty(id)) {
            val interval = try {
                listen(id)
            } catch (cause: SerializationException) {
                logger.warning({ "$name with $id 数据加载异常，请汇报给开发者" }, cause)
                slow
            } catch (e: Throwable) {
                logger.warning({ "$name with $id fail." }, e)
                slow
            }
            delay(interval)
        }
    }

    protected open fun removeListener(id: Long) = jobs.remove(id)?.cancel()

    open suspend fun initTask(id: Long): BiliTask {
        return BiliTask(name = BiliTaskData.user.getOrPut(key = id) { client.getUserInfo(uid = id).name })
    }

    override suspend fun task(id: Long, subject: Contact) = mutex.withLock {
        val old = try {
            tasks[id] ?: initTask(id)
        } catch (cause: Throwable) {
            logger.warning(cause)
            return@withLock "发生错误, ${cause.message}"
        }
        val new = old.copy(contacts = old.contacts + subject.delegate)
        tasks[id] = new
        jobs.compute(id) { _, job ->
            job?.takeIf { it.isActive } ?: addListener(id)
        }
        "对@${new.name}#${id}的监听任务, 添加完成"
    }

    override suspend fun remove(id: Long, subject: Contact) = mutex.withLock {
        val old = tasks[id] ?: return@withLock "任务不存在"
        val new = old.copy(contacts = old.contacts - subject.delegate)
        if (new.contacts.isEmpty()) {
            jobs[id]?.cancel()
            tasks.remove(id)
        } else {
            tasks[id] = new
        }
        "对@${new.name}#${id}的监听任务, 取消完成"
    }

    override suspend fun list(subject: Contact): String = mutex.withLock {
        buildString {
            appendLine("监听状态:")
            for ((id, info) in tasks) {
                if (subject.delegate in info.contacts) {
                    appendLine("@${info.name}#$id -> ${info.last} | ${jobs[id]}")
                }
            }
        }
    }

    override fun start() {
        for ((id, info) in tasks) {
            if (info.contacts.isEmpty()) continue
            try {
                jobs[id] = addListener(id)
            } finally {
                logger.info { "$name with $id start $info" }
            }
        }
    }

    override fun stop() {
        coroutineContext.cancelChildren()
        jobs.clear()
    }
}

sealed class Loader<T : Entry>(name: String) : AbstractTasker<T>(name) {

    protected abstract val limit: Int

    protected abstract suspend fun load(id: Long): List<T>

    protected abstract fun T.time(): OffsetDateTime

    protected abstract fun T.check(): Boolean

    protected abstract suspend fun List<T>.near(): Boolean

    protected open fun BiliTask.send(list: List<T>) = contacts.map { delegate ->
        async {
            try {
                val contact = requireNotNull(findContact(delegate)) { "找不到联系人 $delegate" }
                contact.sendMessage(buildForwardMessage(contact) {
                    for (item in list) {
                        contact.bot at item.time().toEpochSecond().toInt() says item.build(contact)
                    }
                })
            } catch (e: Throwable) {
                logger.warning({ "对[${delegate}]构建消息失败" }, e)
                null
            }
        }
    }

    override suspend fun listen(id: Long): Long {
        val load = load(id)

        mutex.withLock {
            val task = tasks.getValue(id)
            var last = task.last
            // XXX: 内容较多时，合并成转发消息 https://github.com/cssxsh/bilibili-helper/issues/78
            val list: MutableList<T> = ArrayList()
            for (item in load) {
                if (item.time() > task.last && item.check()) {
                    list.add(item)
                    last = maxOf(item.time(), last)
                }
            }
            if (list.size < limit) {
                for (item in list) {
                    task.send(item)
                }
            } else {
                task.send(list)
            }

            tasks[id] = task.copy(last = last)
        }

        return if (load.near()) fast else slow
    }
}

sealed class Waiter<T : Entry>(name: String) : AbstractTasker<T>(name) {

    private val states: MutableMap<Long, Boolean> = HashMap()

    protected abstract suspend fun load(id: Long): T

    protected abstract suspend fun T.success(): Boolean

    protected abstract suspend fun T.near(): Boolean

    protected abstract suspend fun T.last(): OffsetDateTime

    override suspend fun listen(id: Long): Long {
        val item = load(id)
        val state = states.put(id, item.success())

        if (state != true && states[id]!!) {
            mutex.withLock {
                val task = tasks.getValue(id)
                tasks[id] = task.copy(last = item.last())
                task.send(item)
            }
            delay(slow)
        }

        return if (item.near()) fast else slow
    }
}

private fun List<LocalTime>.near(slow: Long, now: LocalTime = LocalTime.now()): Boolean {
    return any { abs(it.toSecondOfDay() - now.toSecondOfDay()) * 1000 < slow }
}

private const val Minute = 60 * 1000L

object BiliVideoLoader : Loader<Video>(name = "VideoTasker") {
    override val tasks: MutableMap<Long, BiliTask> get() = BiliTaskData.video

    override val limit: Int get() = BiliHelperSettings.max

    override val fast get() = Minute

    override val slow get() = BiliHelperSettings.video * Minute

    override suspend fun load(id: Long) = client.getVideos(uid = id).list.videos

    override fun Video.time(): OffsetDateTime = datetime

    override fun Video.check(): Boolean {
        // XXX: 视频类型检查 https://github.com/cssxsh/bilibili-helper/issues/82
        return !with(BiliTaskerConfig) {
            tid in videoForbidType ||
                (isPay && videoForbidPay) ||
                (isUnionVideo && videoForbidUnion) ||
                (isSteinsGate && videoForbidInteract) ||
                (isLivePlayback && videoForbidPlayback)
        }
    }

    override suspend fun List<Video>.near() = map { it.datetime.toLocalTime() }.near(slow)

    override suspend fun Video.build(contact: Contact) = content(contact)
}

object BiliDynamicLoader : Loader<DynamicInfo>(name = "DynamicTasker") {
    override val tasks: MutableMap<Long, BiliTask> get() = BiliTaskData.dynamic

    override val limit: Int get() = BiliHelperSettings.max

    override val fast get() = Minute

    override val slow = BiliHelperSettings.dynamic * Minute

    private val regexes get() = BiliTaskerConfig.dynamicForbidRegexes.asSequence().map { it.toRegex() }

    private val types get() = BiliTaskerConfig.dynamicForbidType

    override suspend fun load(id: Long) = client.getSpaceHistory(uid = id).dynamics

    override fun DynamicInfo.time(): OffsetDateTime = datetime

    override fun DynamicInfo.check(): Boolean = !match()

    private fun DynamicCard.match(): Boolean {
        // XXX: 检查动态类型，文本 https://github.com/cssxsh/bilibili-helper/issues/82
        return when (detail.type) {
            DynamicType.REPLY -> {
                val decode = decode<DynamicReply>()
                regexes.any { regex -> regex in decode.content } || decode.match()
            }
            DynamicType.PICTURE -> {
                val decode = decode<DynamicPicture>()
                regexes.any { regex -> regex in decode.content }
            }
            DynamicType.TEXT -> {
                val decode = decode<DynamicText>()
                regexes.any { regex -> regex in decode.content }
            }
            else -> detail.type in types
        }
    }

    override suspend fun List<DynamicInfo>.near() = map { it.datetime.toLocalTime() }.near(slow)

    override suspend fun DynamicInfo.build(contact: Contact) = content(contact)
}

object BiliLiveWaiter : Waiter<BiliLiveInfo>(name = "LiveWaiter") {
    override val tasks: MutableMap<Long, BiliTask> get() = BiliTaskData.live

    override val fast get() = Minute

    override val slow get() = BiliHelperSettings.live * Minute

    private val record: MutableMap<Long, Long> get() = BiliTaskData.map

    private val Contact.permitteeId: PermitteeId
        get() = when (this) {
            is Group -> AbstractPermitteeId.ExactGroup(id)
            is Member -> AbstractPermitteeId.ExactMember(group.id, id)
            is Stranger -> AbstractPermitteeId.ExactStranger(id)
            is User -> AbstractPermitteeId.ExactUser(id)
            is OtherClient -> AbstractPermitteeId.AnyOtherClient
            else -> AbstractPermitteeId.AnyContact
        }

    private fun sleep(target: PermitteeId, time: LocalTime = LocalTime.now()): Boolean {
        return BiliTaskerConfig.liveSleep.any { (permitteeId, interval) ->
            permitteeId.hasChild(target) && time in interval
        }
    }

    private fun at(group: Group, time: LocalTime = LocalTime.now()): Message = buildMessageChain {
        fun check(target: PermitteeId, time: LocalTime): Boolean {
            return BiliTaskerConfig.liveAt.any { (permitteeId, interval) ->
                permitteeId.hasChild(target) && time in interval
            }
        }
        if (check(target = group.permitteeId, time = time)) {
            append(AtAll)
            if (group.botPermission < MemberPermission.ADMINISTRATOR) {
                try {
                    @Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
                    append(net.mamoe.mirai.internal.message.ForceAsLongMessage)
                } catch (_: Throwable) {
                    //
                }
            }
        }
        group.members.forEach { member ->
            if (check(target = member.permitteeId, time = time)) {
                append(At(member))
            }
        }
    }

    override suspend fun load(id: Long): BiliLiveInfo {
        val roomId = record.getOrPut(id) {
            requireNotNull(client.getUserInfo(uid = id).liveRoom) { "用户 $id 没有开通直播间" }.roomId
        }
        return client.getLiveInfo(roomId = roomId)
    }

    override suspend fun BiliLiveInfo.success(): Boolean {
        return if (liveStatus) {
            System.currentTimeMillis() - last().toInstant().toEpochMilli() < 3 * fast + slow
        } else {
            record.remove(roomId)
            false
        }
    }

    override suspend fun BiliLiveInfo.build(contact: Contact): Message =when {
        sleep(target = contact.permitteeId) -> EmptyMessageChain
        contact is Group -> content(contact) + at(contact)
        else -> content(contact)
    }

    override suspend fun BiliLiveInfo.near(): Boolean = LocalTime.now().minute < BiliHelperSettings.live

    override suspend fun BiliLiveInfo.last(): OffsetDateTime = datetime

    override suspend fun initTask(id: Long): BiliTask {
        val live = try {
            client.getUserInfo(uid = id).liveRoom
                ?: throw NoSuchElementException("Live Room by https://space.bilibili.com/${id}")
        } catch (cause: NoSuchElementException) {
            logger.warning { cause.message }
            client.getLiveInfo(roomId = id)
        }
        return BiliTask(name = live.uname)
    }
}

object BiliSeasonWaiter : Waiter<BiliSeasonInfo>(name = "SeasonWaiter") {
    override val tasks: MutableMap<Long, BiliTask> get() = BiliTaskData.season

    override val fast get() = Minute

    override val slow = BiliHelperSettings.season * Minute

    private val record: MutableMap<Long, Video> = HashMap()

    private suspend fun video(aid: Long) = record.getOrPut(aid) { client.getVideoInfo(aid = aid) }

    override suspend fun load(id: Long): BiliSeasonInfo = client.getSeasonInfo(seasonId = id)

    override suspend fun BiliSeasonInfo.success(): Boolean {
        return episodes.isNotEmpty() && last() > tasks.getValue(seasonId).last
    }

    override suspend fun BiliSeasonInfo.build(contact: Contact): Message {
        val episode = episodes.maxByOrNull { it.published ?: video(aid = it.aid).created }!!
        return content(contact) + "\n" + video(aid = episode.aid).content(contact)
    }

    override suspend fun BiliSeasonInfo.near(): Boolean {
        return episodes.map { (it.datetime ?: video(aid = it.aid).datetime).toLocalTime() }.near(slow)
    }

    override suspend fun BiliSeasonInfo.last(): OffsetDateTime {
        return episodes.maxOf { it.datetime ?: video(aid = it.aid).datetime }
    }

    override suspend fun initTask(id: Long): BiliTask = BiliTask(name = client.getSeasonInfo(seasonId = id).title)
}