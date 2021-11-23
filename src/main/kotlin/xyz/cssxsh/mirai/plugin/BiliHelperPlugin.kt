package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*

object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription(id = "xyz.cssxsh.mirai.plugin.bilibili-helper", version = "1.2.5") {
        name("bilibili-helper")
        author("cssxsh")
    }
) {

    internal val selenium: Boolean by lazy {
        BiliHelperSettings.selenium && try {
            MiraiSeleniumPlugin.setup()
        } catch (exception: NoClassDefFoundError) {
            logger.warning { "相关类加载失败，请安装 https://github.com/cssxsh/mirai-selenium-plugin $exception" }
            false
        } catch (exception: UnsupportedOperationException) {
            logger.warning { "截图模式，请安装 Chrome 或者 Firefox 浏览器 $exception" }
            false
        } catch (it: Throwable) {
            logger.warning { "截图模式，初始化浏览器驱动失败 $it" }
            false
        }
    }

    @OptIn(ConsoleExperimentalApi::class)
    private fun <T : PluginConfig> T.save() = loader.configStorage.store(this@BiliHelperPlugin, this)

    override fun onEnable() {
        BiliTaskData.reload()
        BiliHelperSettings.reload()
        BiliHelperSettings.save()
        BiliCleanerConfig.reload()
        BiliCleanerConfig.save()

        client.load()

        for (command in BiliHelperCommand) {
            command.register()
        }

        if (selenium) {
            logger.info { "加载 SeleniumToolConfig" }
            BiliSeleniumConfig.reload()
            BiliSeleniumConfig.save()
            driver = MiraiSeleniumPlugin.driver(config = BiliSeleniumConfig)
        }

        globalEventChannel().subscribeOnce<BotOnlineEvent> {
            BiliListener.subscribe()
            for (task in BiliTasker) {
                task.start()
            }
            BiliCleaner.start()

            if (selenium) {
                BiliSeleniumConfig.runCatching {
                    driver.setHome(page = home)
                }.onSuccess { version ->
                    logger.info { "Selenium Browser Version $version" }
                }.onFailure {
                    logger.warning({ "设置主页失败" }, it)
                }
            }
        }
    }

    override fun onDisable() {
        for (command in BiliHelperCommand) {
            command.unregister()
        }

        BiliListener.stop()

        if (selenium) {
            driver.quit()
        }

        runBlocking(coroutineContext) {
            for (task in BiliTasker) {
                task.stop()
            }
        }
        BiliCleaner.stop()
        client.save()
    }
}