package xyz.cssxsh.mirai.plugin

import io.github.karlatemp.mxlib.MxLib
import io.github.karlatemp.mxlib.logger.NopLogger
import io.github.karlatemp.mxlib.selenium.MxSelenium
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import org.openqa.selenium.remote.RemoteWebDriver
import xyz.cssxsh.bilibili.BiliClient
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.BiliHelperSettings
import xyz.cssxsh.mirai.plugin.data.BiliTaskData
import xyz.cssxsh.mirai.plugin.data.SeleniumToolConfig
import xyz.cssxsh.mirai.plugin.tools.*
import xyz.cssxsh.mirai.plugin.tools.DriverConsumer
import java.util.logging.Level
import kotlin.time.toJavaDuration

object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "0.1.0-dev-1") {
        name("bilibili-helper")
        author("cssxsh")
    }
) {

    val client by lazy { BiliClient() }

    lateinit var driver: RemoteWebDriver

    init {
        // System.setProperty("webdriver.http.factory", "ktor")
        setSeleniumLogLevel(Level.OFF)
    }

    override fun PluginComponentStorage.onLoad() {
        MxLib.setLoggerFactory { name -> NopLogger(name) }
        MxLib.setDataStorage(dataFolder)
    }

    override fun onEnable() {
        BiliTaskData.reload()
        SeleniumToolConfig.reload()
        BiliHelperSettings.reload()

        BiliListener.subscribe()

        BiliInfoCommand.register()
        BiliDynamicCommand.register()
        BiliVideoCommand.register()
        BiliLiveCommand.register()
        BiliSeasonCommand.register()

        driver = MxSelenium.newDriver(null, DriverConsumer).init()

        BiliTasker.startAll()
    }

    override fun onDisable() {
        BiliInfoCommand.unregister()
        BiliDynamicCommand.unregister()
        BiliVideoCommand.unregister()
        BiliLiveCommand.unregister()
        BiliSeasonCommand.unregister()

        BiliListener.stop()

        driver.quit()

        BiliTasker.stopAll()
    }
}