package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.code.MiraiCode.serializeToMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import xyz.cssxsh.bilibili.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*

private val regex = """#([A-z]+)""".toRegex()

internal suspend fun Entry.content(contact: Contact): MessageChain {
    val template = BiliTemplate[this::class.java]
    val content = regex.findAll(template).fold(template) { current, result ->
        val replacement = when (val name = result.groupValues.first()) {
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
        current.replaceRange(result.range, replacement)
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
        else -> TODO("${this::class.java.simpleName}.images")
    }
}

internal suspend fun Entry.detail(contact: Contact): String {
    return when (this) {
        is DynamicCard -> {
            when (detail.type) {
                DynamicType.NONE -> "不支持的类型${detail.type}"
                DynamicType.REPLY -> decode<DynamicReply>().content(contact).serializeToMiraiCode()
                DynamicType.PICTURE -> text(contact)
                DynamicType.TEXT -> text(contact)
                DynamicType.VIDEO -> decode<DynamicVideo>().content(contact).serializeToMiraiCode()
                DynamicType.ARTICLE -> decode<DynamicArticle>().content(contact).serializeToMiraiCode()
                DynamicType.MUSIC -> decode<DynamicMusic>().content(contact).serializeToMiraiCode()
                DynamicType.EPISODE -> decode<DynamicEpisode>().content(contact).serializeToMiraiCode()
                DynamicType.BANGUMI -> decode<DynamicEpisode>().content(contact).serializeToMiraiCode()
                DynamicType.DELETE -> "源动态已被作者删除"
                DynamicType.SKETCH -> decode<DynamicSketch>().content(contact).serializeToMiraiCode()
                DynamicType.LIVE -> decode<DynamicLive>().content(contact).serializeToMiraiCode()
                DynamicType.LIVE_END -> "直播结束了"
                else -> "<${detail.id}>[${detail.type}] 不支持的类型 请联系开发者"
            }
        }
        is BiliRoomInfo -> {
            val live = client.getUserInfo(uid = uid).liveRoom
            live.start = datetime
            live.content(contact).serializeToMiraiCode()
        }
        else -> TODO("${this::class.java.simpleName}.detail")
    }
}

internal suspend fun Entry.screenshot(contact: Contact): String {
    return when (this) {
        is DynamicInfo -> screenshot(contact).serializeToMiraiCode()
        else -> TODO("${this::class.java.simpleName}.screenshot")
    }
}

internal suspend fun DynamicCard.text(contact: Contact): String {

    val content = when (detail.type) {
        DynamicType.PICTURE -> decode<DynamicPicture>().detail.description
        DynamicType.TEXT -> decode<DynamicText>().detail.content
        else -> ""
    }

    val details = (display ?: return content).emojis

    return details.fold(content) { current, emoji ->
        try {
            val code = emoji.cache(contact).serializeToMiraiCode()
            current.replace(emoji.text, code)
        } catch (cause: Throwable) {
            logger.warning("获取BILI表情${emoji.text}图片失败, $cause")
            current
        }
    }
}