package xyz.cssxsh.mirai.bilibili

import net.mamoe.mirai.utils.warning
import xyz.cssxsh.bilibili.data.*
import java.io.File
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

object BiliTemplate {
    private lateinit var last: File
    private val cache = HashMap<Class<*>, String>()
    internal const val DATE_TIME_PATTERN = "xyz.cssxsh.mirai.bilibili.datetime"

    init {
        cache[Article::class.java] = """
            专栏: #title
            链接: #link
            简介: #summary
            #images
            状态: 
            #status
        """.trimIndent()
        cache[ArticleStatus::class.java] = """
            点赞: #like 硬币: #coin 收藏: #favorite
            阅读: #view 评论: #reply 分享: #share
        """.trimIndent()
        cache[UserInfo::class.java] = """
            #images
            @#name##mid
            直播: #live
            等级: #level
            直播: #live
            简介: 
            #sign
        """.trimIndent()
        cache[DynamicInfo::class.java] = """
            @#username##uid 动态
            时间: #datetime
            链接: #link
            #detail
            #images
        """.trimIndent()
        cache[DynamicReply::class.java] = """
            RT @#username##uid
            #content
            <===================>
            #detail
            #images
        """.trimIndent()
        cache[DynamicEpisode::class.java] = """
            更新: #index - #title
            链接: #share
            #description
            #images
        """.trimIndent()
        cache[DynamicSketch::class.java] = """
            音乐: #title
            链接: #link
            #content
            #images
        """.trimIndent()
        cache[DynamicMusic::class.java] = """
            音乐: #title
            作者: #author
            介绍: #intro
            链接: #link
            #images
        """.trimIndent()
        cache[Live::class.java] = """
            房间: #title - #uname##uid
            开播: #liveStatus
            链接: #share
            人气: #online
            开播时间: #start
            #images
        """.trimIndent()
        cache[Media::class.java] = """
            #type: #title
            评分: #rating
            详情: #share
            链接: #link
            #images
        """.trimIndent()
        cache[Video::class.java] = """
            标题: #title
            作者: #author##mid
            时间: #datetime
            时长: #length
            链接: #link
            状态: 
            #status
            简介: 
            #description
            #images
        """.trimIndent()
        cache[VideoStatus::class.java] = """
            点赞: #like 硬币: #coin 收藏: #favorite
            弹幕: #danmaku 评论: #reply 分享: #share
            观看: #view 排行: #nowRank
        """.trimIndent()
        cache[BiliRoomInfo::class.java] = """
            #detail
        """.trimIndent()
        cache[Rating::class.java] = """
            #score/#count
        """.trimIndent()
    }

    fun reload(folder: File = last) {
        folder.mkdirs()
        val templates = folder.listFiles { file -> file.extension == "template" }.orEmpty()
        for (template in templates) {
            val name = "${Entry::class.java.packageName}.${template.nameWithoutExtension}"
            val clazz = Class.forName(name, true, Entry::class.java.classLoader)
            cache[clazz] = template.readText()
        }
        for ((clazz, text) in cache) {
            val template = folder.resolve("${clazz.simpleName}.template")
            if (template.exists().not()) template.writeText(text)
        }
    }

    fun selenium() = cache.values.any { "#screenshot" in it }

    fun formatter(pattern: String = System.getProperty(DATE_TIME_PATTERN)): DateTimeFormatter {
        return when (pattern) {
            "ISO_LOCAL_DATE" -> DateTimeFormatter.ISO_LOCAL_DATE
            "ISO_OFFSET_DATE" -> DateTimeFormatter.ISO_OFFSET_DATE
            "ISO_DATE" -> DateTimeFormatter.ISO_DATE
            "ISO_LOCAL_TIME" -> DateTimeFormatter.ISO_LOCAL_TIME
            "ISO_OFFSET_TIME" -> DateTimeFormatter.ISO_OFFSET_TIME
            "ISO_TIME" -> DateTimeFormatter.ISO_TIME
            "ISO_LOCAL_DATE_TIME" -> DateTimeFormatter.ISO_LOCAL_DATE_TIME
            "ISO_OFFSET_DATE_TIME" -> DateTimeFormatter.ISO_OFFSET_DATE_TIME
            "ISO_ZONED_DATE_TIME" -> DateTimeFormatter.ISO_ZONED_DATE_TIME
            "ISO_DATE_TIME" -> DateTimeFormatter.ISO_DATE_TIME
            "ISO_ORDINAL_DATE" -> DateTimeFormatter.ISO_ORDINAL_DATE
            "ISO_WEEK_DATE" -> DateTimeFormatter.ISO_WEEK_DATE
            "ISO_INSTANT" -> DateTimeFormatter.ISO_INSTANT
            "BASIC_ISO_DATE" -> DateTimeFormatter.BASIC_ISO_DATE
            "RFC_1123_DATE_TIME" -> DateTimeFormatter.RFC_1123_DATE_TIME
            else -> DateTimeFormatter.ofPattern(pattern)
        }
    }

    operator fun get(clazz: Class<out Entry>): String {
        return cache[clazz]
            ?: cache.entries.find { (superclass, _) -> superclass.isAssignableFrom(clazz) }?.value
            ?: throw NoSuchElementException("template of ${clazz.name}")
    }
}