package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group

@Serializable
data class ContactInfo(
    @SerialName("id")
    val id: Long,
    @SerialName("bot")
    val bot: Long,
    @SerialName("type")
    val type: ContactType
)

@Serializable
enum class ContactType {
    GROUP,
    FRIEND
}

fun Contact.toContactInfo() = ContactInfo(
    id = id,
    bot = bot.id,
    type = when (this) {
        is Group -> ContactType.GROUP
        is Friend -> ContactType.FRIEND
        else -> throw IllegalArgumentException("不支持的联系人, $this")
    }
)

fun ContactInfo.getContact(): Contact = Bot.getInstance(bot).let { bot ->
    when (type) {
        ContactType.GROUP -> bot.getGroupOrFail(id)
        ContactType.FRIEND -> bot.getFriendOrFail(id)
    }
}