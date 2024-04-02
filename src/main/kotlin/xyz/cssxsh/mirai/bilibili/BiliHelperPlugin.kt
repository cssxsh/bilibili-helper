package xyz.cssxsh.mirai.bilibili

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.bilibili.data.*

@PublishedApi
internal object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription(id = "xyz.cssxsh.mirai.plugin.bilibili-helper", version = "1.8.0") {
        name("bilibili-helper")
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-selenium-plugin", ">= 2.1.0", true)
    }
) {

    override fun PluginComponentStorage.onLoad() {
        // run after auto login
        runAfterStartup {
            if (BiliHelperSettings.refresh) BiliTasker.refresh()
            for (task in BiliTasker) task.start()
            BiliCleaner.start()

            BiliTemplate.selenium() && SetupSelenium
        }
    }

    private val commands: List<Command> by services()
    private val data: List<PluginData> by services()
    private val config: List<PluginConfig> by services()
    private val listeners: List<ListenerHost> by services()

    override fun onEnable() {

        for (config in config) config.reload()
        for (data in data) data.reload()
        BiliTemplate.reload(configFolder.resolve("Template"))

        System.setProperty(BiliTemplate.DATE_TIME_PATTERN, BiliTaskerConfig.pattern)
        System.setProperty(EXCEPTION_JSON_CACHE, dataFolder.absolutePath)

        client.load()

        for (command in commands) command.register()

        logger.info { "如果要B站动态的截图内容，请修改 DynamicInfo.template, 添加 #screenshot" }
        logger.info { "如果要B站专栏的截图内容，请修改 Article.template, 添加 #screenshot" }

        for (listener in listeners) (listener as SimpleListenerHost).registerTo(globalEventChannel())

        launch {
            loadCookie()
            loadEmoteData()
        }
        launch {
            while (isActive) {
                val salt = try {
                    client.salt()
                } catch (_: Throwable) {
                    ""
                }
                logger.info("salt refresh : ${salt.ifEmpty { "<empty>" }}")
                resolveDataFile("salt.txt").writeText(salt)
                delay(60 * 60 * 1000)
            }
        }
    }

    override fun onDisable() {
        for (command in commands) command.unregister()

        for (listener in listeners) (listener as SimpleListenerHost).cancel()

        for (task in BiliTasker) task.stop()
        BiliCleaner.stop()
        client.save()
        coroutineContext.cancelChildren()
    }
}