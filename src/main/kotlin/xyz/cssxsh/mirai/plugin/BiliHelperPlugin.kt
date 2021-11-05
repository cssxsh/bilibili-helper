package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import org.openqa.selenium.remote.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.mirai.plugin.tools.*

object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "1.2.0") {
        name("bilibili-helper")
        author("cssxsh")
    }
) {

    lateinit var driver: RemoteWebDriver
        private set

    val selenium: Boolean by lazy {
        SeleniumToolConfig.setup && SeleniumToolConfig.runCatching {
            setupSelenium(dataFolder, browser)
        }.onFailure {
            if (it is UnsupportedOperationException) {
                logger.warning { "截图模式，请安装 Chrome 或者 Firefox 浏览器 $it" }
            } else {
                logger.warning { "截图模式，初始化浏览器驱动失败 $it" }
            }
        }.isSuccess
    }

    private fun <T : PluginConfig> T.save() = loader.configStorage.store(this@BiliHelperPlugin, this)

    override fun onEnable() {
        BiliTaskData.reload()
        SeleniumToolConfig.reload()
        SeleniumToolConfig.save()
        BiliHelperSettings.reload()
        BiliHelperSettings.save()
        BiliCleanerConfig.reload()
        BiliCleanerConfig.save()

        client.load()
        BiliListener.subscribe()

        BiliInfoCommand.register()
        BiliDynamicCommand.register()
        BiliVideoCommand.register()
        BiliLiveCommand.register()
        BiliSeasonCommand.register()
        BiliSearchCommand.register()

        if (selenium) {
            driver = RemoteWebDriver(config = SeleniumToolConfig)
            launch(SupervisorJob()) {
                driver.runCatching {
                    home(page = SeleniumToolConfig.home)
                }.onSuccess { version ->
                    logger.info { "driver agent $version" }
                }.onFailure {
                    logger.warning { "设置主页失败" }
                }
            }
        }

        globalEventChannel().subscribeOnce<BotOnlineEvent> {
            BiliTasker.startAll()
            BiliCleaner.start()
        }
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

        runBlocking {
            BiliTasker.stopAll()
        }
        BiliCleaner.stop()
        client.save()
    }
}