package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import kotlin.time.*

object BilibiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "0.1.0-dev-1") {
        name("bilibili-helper")
        author("cssxsh")
    }
) {

    internal lateinit var client : BilibiliClient
        private set

    @ConsoleExperimentalApi
    override val autoSaveIntervalMillis: LongRange
        get() = (3).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds()

    @ConsoleExperimentalApi
    override fun onEnable() {
        BilibiliTaskData.reload()
        SeleniumToolConfig.reload()
        BilibiliHelperSettings.reload()

        client = BilibiliClient(BilibiliHelperSettings.initCookies)

        BilibiliSubscribeCommand.start()
        BilibiliInfoCommand.start()

        BilibiliSubscribeCommand.register()
        BilibiliInfoCommand.register()
    }

    @ConsoleExperimentalApi
    override fun onDisable() {
        BilibiliSubscribeCommand.unregister()
        BilibiliInfoCommand.unregister()

        BilibiliSubscribeCommand.stop()
        BilibiliInfoCommand.stop()
    }
}