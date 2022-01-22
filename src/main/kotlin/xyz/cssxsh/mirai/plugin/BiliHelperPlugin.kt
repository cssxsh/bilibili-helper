package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*

object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription(id = "xyz.cssxsh.mirai.plugin.bilibili-helper", version = "1.4.0") {
        name("bilibili-helper")
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-selenium-plugin", true)
    }
) {

    override fun onEnable() {
        BiliTaskData.reload()
        BiliHelperSettings.reload()
        BiliHelperSettings.save()
        BiliCleanerConfig.reload()
        BiliCleanerConfig.save()
        BiliTemplate.reload(configFolder.resolve("Template").apply { mkdirs() })

        client.load()

        for (command in BiliHelperCommand) {
            command.register()
        }

        logger.info { "如果要B站动态的截图内容，请修改 DynamicInfo.template, 添加 #screenshot" }

        if (SetupSelenium) {
            launch(SupervisorJob()) {
                BiliSeleniumConfig.reload()
                BiliSeleniumConfig.save()
            }
        }

        globalEventChannel().subscribeOnce<BotOnlineEvent> {
            BiliListener.subscribe()
            for (task in BiliTasker) {
                task.start()
            }
            BiliCleaner.start()
        }
    }

    override fun onDisable() {
        for (command in BiliHelperCommand) {
            command.unregister()
        }

        BiliListener.stop()

        runBlocking(coroutineContext) {
            for (task in BiliTasker) {
                task.stop()
            }
        }
        BiliCleaner.stop()
        client.save()
    }
}