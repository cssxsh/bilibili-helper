package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.code.MiraiCode.serializeToMiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import kotlin.coroutines.*

private val regex = """#([A-z]+)""".toRegex()

internal suspend fun Entry.content(contact: Contact): MessageChain {
    val template = BiliTemplate[this::class.java]
    var content = template
    var pos = 0
    while (coroutineContext.isActive) {
        val result = regex.find(content, pos) ?: break
        val (name) = result.destructured
        val replacement = when (name) {
            "images" -> images(contact)
            "detail" -> detail(contact)
            "screenshot" -> screenshot(contact)
            else -> {
                val member = this::class.members.find { it.name == name }
                    ?: throw NoSuchElementException("${this::class.simpleName} no member $name")
                val value = member.call(this)
                if (value is Entry) {
                    value.content(contact).serializeToMiraiCode()
                } else {
                    value.toString()
                }
            }
        }
        content = content.replaceRange(result.range, replacement)
        pos = result.range.first + replacement.length
    }

    return content.deserializeMiraiCode(contact)
}

internal suspend fun Entry.images(contact: Contact): String {
    return when (this) {
        is Live -> getCover(contact).serializeToMiraiCode()
        is Season -> getCover(contact).serializeToMiraiCode()
        is UserInfo -> getFace(contact).serializeToMiraiCode()
        is Video -> getCover(contact).serializeToMiraiCode()
        is Article -> getImages(contact).serializeToMiraiCode()
        is Episode -> getCover(contact).serializeToMiraiCode()
        is DynamicCard -> getImages(contact).serializeToMiraiCode()
        is DynamicMusic -> getCover(contact).serializeToMiraiCode()
        is DynamicSketch -> getCover(contact).serializeToMiraiCode()
        else -> throw NoSuchElementException("${this::class.java.simpleName}.images")
    }
}

internal suspend fun Entry.detail(contact: Contact): String {
    return when (this) {
        is DynamicCard -> detail(contact)
        is BiliRoomInfo -> client.getUserInfo(uid = uid)
            .liveRoom!!.apply { start = this@detail.datetime }
            .content(contact).serializeToMiraiCode()
        else -> throw NoSuchElementException("${this::class.java.simpleName}.detail")
    }
}

internal suspend fun Entry.screenshot(contact: Contact): String {
    return when (this) {
        is DynamicInfo -> screenshot(contact).serializeToMiraiCode()
        is Article -> screenshot(contact).serializeToMiraiCode()
        else -> throw NoSuchElementException("${this::class.java.simpleName}.screenshot")
    }
}

internal suspend fun DynamicCard.detail(contact: Contact): String {
    return when (detail.type) {
        DynamicType.NONE -> "不支持的类型${detail.type}"
        DynamicType.REPLY -> decode<DynamicReply>()
            .apply { content = content(this@detail.display, contact) }
            .content(contact).serializeToMiraiCode()
        DynamicType.PICTURE -> decode<DynamicPicture>().content(display, contact)
        DynamicType.TEXT -> decode<DynamicText>().content(display, contact)
        DynamicType.VIDEO -> decode<DynamicVideo>().content(contact).serializeToMiraiCode()
        DynamicType.ARTICLE -> decode<DynamicArticle>().content(contact).serializeToMiraiCode()
        DynamicType.MUSIC -> decode<DynamicMusic>().content(contact).serializeToMiraiCode()
        DynamicType.EPISODE, DynamicType.BANGUMI, DynamicType.TV -> decode<DynamicEpisode>()
            .content(contact).serializeToMiraiCode()
        DynamicType.DELETE -> "源动态已被作者删除"
        DynamicType.SKETCH -> decode<DynamicSketch>().content(contact).serializeToMiraiCode()
        DynamicType.LIVE -> decode<DynamicLive>().content(contact).serializeToMiraiCode()
        DynamicType.LIVE_END -> "直播结束了"
        else -> "<${detail.id}>[${detail.type}] 不支持的类型 请联系开发者"
    }
}

internal suspend fun DynamicEmojiContent.content(display: DynamicDisplay?, contact: Contact): String {
    if (display == null) return content
    return display.emoji.details.fold(content) { current, emoji ->
        try {
            val code = emoji.cache(contact).serializeToMiraiCode()
            current.replace(emoji.text, code)
        } catch (cause: Throwable) {
            logger.warning({ "获取BILI表情${emoji.text}图片失败" }, cause)
            current
        }
    }
}