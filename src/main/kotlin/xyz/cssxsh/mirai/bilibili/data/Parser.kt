package xyz.cssxsh.mirai.bilibili.data

import com.cronutils.descriptor.*
import com.cronutils.model.*
import com.cronutils.model.definition.*
import com.cronutils.model.time.*
import com.cronutils.parser.*
import net.mamoe.mirai.console.command.descriptor.*
import java.util.*

internal const val CRON_TYPE_KEY = "xyz.cssxsh.mirai.cron.type"

val DefaultCronParser: CronParser by lazy {
    val type = CronType.valueOf(System.getProperty(CRON_TYPE_KEY, "QUARTZ"))
    CronParser(CronDefinitionBuilder.instanceDefinitionFor(type))
}

internal const val CRON_LOCALE_KEY = "xyz.cssxsh.mirai.cron.locale"

val DefaultCronDescriptor: CronDescriptor by lazy {
    val locale = System.getProperty(CRON_LOCALE_KEY)?.let { Locale.forLanguageTag(it) } ?: Locale.getDefault()
    CronDescriptor.instance(locale)
}

fun Cron.asData(): DataCron = this as? DataCron ?: DataCron(delegate = this)

fun Cron.toExecutionTime(): ExecutionTime = ExecutionTime.forCron((this as? DataCron)?.delegate ?: this)

fun Cron.description(): String = DefaultCronDescriptor.describe(this)

val BiliCommandArgumentContext: CommandArgumentContext = buildCommandArgumentContext {
    Cron::class with { text ->
        try {
            DefaultCronParser.parse(text)
        } catch (cause: Exception) {
            throw CommandArgumentParserException(
                message = cause.message ?: "Cron 表达式读取错误，请注意是否附带了双引号，建议找在线表达式生成器生成",
                cause = cause
            )
        }
    }
}