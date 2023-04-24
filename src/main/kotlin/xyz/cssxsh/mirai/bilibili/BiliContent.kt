package xyz.cssxsh.mirai.bilibili

import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import java.time.temporal.*

internal suspend fun Entry.content(contact: Contact): MessageChain {
    val template = BiliTemplate[this::class.java]
    val entry = this
    return buildMessageChain {
        val last = """#([A-z]+)""".toRegex().findAll(template).fold(0) { pos, match ->
            val (name) = match.destructured
            append(template.subSequence(pos, match.range.first))
            val item = when (name) {
                "images" -> images(contact)
                "detail" -> detail(contact)
                "screenshot" -> screenshot(contact)
                "content" -> (entry as DynamicEmojiContent).content(contact)
                else -> {
                    val member = entry::class.members.find { it.name == name }
                        ?: throw NoSuchElementException("${entry::class.simpleName} no member $name")
                    when (val value = member.call(entry)) {
                        is Entry -> value.content(contact)
                        is TemporalAccessor -> BiliTemplate.formatter().format(value).toPlainText()
                        else -> value.toString().toPlainText()
                    }
                }
            }
            add(item)
            match.range.last + 1
        }
        append(template.subSequence(last, template.length))
    }
}

internal suspend fun Entry.images(contact: Contact): Message {
    return when (this) {
        is Live -> getCover(contact)
        is Season -> getCover(contact)
        is UserInfo -> getFace(contact)
        is Video -> getCover(contact)
        is Article -> getImages(contact).toMessageChain()
        is Episode -> getCover(contact)
        is DynamicCard -> getImages(contact).toMessageChain()
        is DynamicMusic -> getCover(contact)
        is DynamicSketch -> getCover(contact)
        else -> throw NoSuchElementException("${this::class.java.simpleName}.images")
    }
}

internal suspend fun Entry.detail(contact: Contact): Message {
    return when (this) {
        is DynamicCard -> detail(contact)
        is BiliRoomInfo -> client.getUserInfo(uid = uid)
            .liveRoom!!.apply { start = this@detail.datetime }
            .content(contact)
        else -> throw NoSuchElementException("${this::class.java.simpleName}.detail")
    }
}

internal suspend fun Entry.screenshot(contact: Contact): Message {
    return when (this) {
        is DynamicInfo -> screenshot(contact)
        is Article -> screenshot(contact)
        else -> throw NoSuchElementException("${this::class.java.simpleName}.screenshot")
    }
}

internal suspend fun DynamicCard.detail(contact: Contact): Message {
    return when(detail.type) {
        in DynamicType.DYNAMIC_TYPE_NONE -> "源动态已被作者删除".toPlainText()
        in DynamicType.DYNAMIC_TYPE_FORWARD -> (decode<DynamicReply>() as Entry).content(contact)
        in DynamicType.DYNAMIC_TYPE_DRAW -> decode<DynamicPicture>().content(contact)
        in DynamicType.DYNAMIC_TYPE_WORD -> decode<DynamicText>().content(contact)
        in DynamicType.DYNAMIC_TYPE_AV -> decode<DynamicVideo>().content(contact)
        in DynamicType.DYNAMIC_TYPE_ARTICLE -> decode<DynamicArticle>().content(contact)
        in DynamicType.DYNAMIC_TYPE_MUSIC -> decode<DynamicMusic>().content(contact)
        in DynamicType.DYNAMIC_TYPE_PGC -> decode<DynamicEpisode>().content(contact)
        in DynamicType.DYNAMIC_TYPE_COMMON_SQUARE -> decode<DynamicSketch>().content(contact)
        in DynamicType.DYNAMIC_TYPE_LIVE -> decode<DynamicLive>().content(contact)
        in DynamicType.DYNAMIC_TYPE_MEDIALIST -> decode<DynamicMediaList>().content(contact)
        in DynamicType.DYNAMIC_TYPE_LIVE_RCMD -> decode<DynamicLiveRoom>().content(contact)
        in DynamicType.DYNAMIC_TYPE_UGC_SEASON -> decode<DynamicVideo>().content(contact)
        else -> "<${detail.id}>[${detail.type}] 不支持的类型 请联系开发者".toPlainText()
    }
}

internal suspend fun DynamicEmojiContent.content(contact: Contact): Message {
    val emoji = this.emoji ?: return content.toPlainText()
    return buildMessageChain {
        val last = """\[[^]]+]""".toRegex().findAll(content).fold(0) { pos, match ->
            val detail = emoji.details.find { it.text == match.value }
            val next = match.range.last + 1
            if (detail != null) {
                append(content.subSequence(pos, match.range.first))
                try {
                    append(detail.cache(contact))
                } catch (cause: Exception) {
                    logger.warning({ "获取BILI表情${detail.text}图片失败" }, cause)
                    append(detail.text)
                }
            } else {
                append(content.subSequence(pos, next))
            }

            next
        }
        append(content.subSequence(last, content.length))
    }
}