package xyz.cssxsh.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.*
import org.openqa.selenium.remote.RemoteWebDriver
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.mirai.plugin.tools.*

object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "1.0.0") {
        name("bilibili-helper")
        author("cssxsh")
    }
) {

    lateinit var driver: RemoteWebDriver
        private set

    var selenium: Boolean = false
        private set

    override fun PluginComponentStorage.onLoad() {
        selenium = runCatching {
            setupSelenium(dataFolder)
        }.onFailure {
            if (it is UnsupportedOperationException) {
                logger.warning { "请安装 Chrome 或者 Firefox 浏览器 $it" }
            } else {
                logger.warning { "初始化浏览器驱动失败 $it" }
            }
        }.isSuccess
    }

    override fun onEnable() {
        BiliTaskData.reload()
        SeleniumToolConfig.reload()
        BiliHelperSettings.reload()
        BiliCleanerConfig.reload()

        BiliListener.subscribe()

        BiliInfoCommand.register()
        BiliDynamicCommand.register()
        BiliVideoCommand.register()
        BiliLiveCommand.register()
        BiliSeasonCommand.register()
        BiliSearchCommand.register()

        if (selenium) {
            driver = RemoteWebDriver()
        }

        BiliTasker.startAll()
        BiliCleaner.start()
    }

    override fun onDisable() {
        BiliInfoCommand.unregister()
        BiliDynamicCommand.unregister()
        BiliVideoCommand.unregister()
        BiliLiveCommand.unregister()
        BiliSeasonCommand.unregister()
        BiliSearchCommand.unregister()

        BiliListener.stop()

        if (selenium) {
            driver.quit()
        }

        BiliTasker.stopAll()
        BiliCleaner.stop()
    }
}