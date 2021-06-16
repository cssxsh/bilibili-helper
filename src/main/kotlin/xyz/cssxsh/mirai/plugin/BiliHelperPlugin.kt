package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import org.openqa.selenium.remote.RemoteWebDriver
import xyz.cssxsh.bilibili.BiliClient
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.mirai.plugin.tools.*

object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "0.1.0-dev-1") {
        name("bilibili-helper")
        author("cssxsh")
    }
) {

    val client by lazy { BiliClient() }

    lateinit var driver: RemoteWebDriver
        private set

    var selenium: Boolean = false
        private set

    override fun PluginComponentStorage.onLoad() {
        selenium = runCatching { setupSelenium(dataFolder) }.onFailure(logger::warning).isSuccess
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

        if (selenium) {
            driver = RemoteWebDriver()
        }

        BiliTasker.startAll()
    }

    override fun onDisable() {
        BiliInfoCommand.unregister()
        BiliDynamicCommand.unregister()
        BiliVideoCommand.unregister()
        BiliLiveCommand.unregister()
        BiliSeasonCommand.unregister()

        BiliListener.stop()

        if (selenium) {
            driver.quit()
        }

        BiliTasker.stopAll()
    }
}