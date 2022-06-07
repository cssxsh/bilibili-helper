package xyz.cssxsh.mirai.bilibili

import net.mamoe.mirai.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.mirai.bilibili.command.*
import xyz.cssxsh.mirai.bilibili.data.*

object BiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription(id = "xyz.cssxsh.mirai.plugin.bilibili-helper", version = "1.5.1") {
        name("bilibili-helper")
        author("cssxsh")

        dependsOn("xyz.cssxsh.mirai.plugin.mirai-selenium-plugin", ">= 2.1.0", true)
    }
) {

    override fun onEnable() {
        BiliTaskData.reload()
        BiliTaskerConfig.reload()
        BiliHelperSettings.reload()
        BiliHelperSettings.save()
        BiliCleanerConfig.reload()
        BiliCleanerConfig.save()
        BiliTemplate.reload(configFolder.resolve("Template"))

        System.setProperty(BiliTemplate.DATE_TIME_PATTERN, BiliTaskerConfig.pattern)
        System.setProperty(EXCEPTION_JSON_CACHE, dataFolder.absolutePath)

        client.load()

        for (command in BiliHelperCommand) {
            command.register()
        }

        logger.info { "如果要B站动态的截图内容，请修改 DynamicInfo.template, 添加 #screenshot" }
        logger.info { "如果要B站专栏的截图内容，请修改 Article.template, 添加 #screenshot" }

        BiliListener.registerTo(globalEventChannel())

        waitOnline {
            if (BiliHelperSettings.refresh) BiliTasker.refresh()
            for (task in BiliTasker) {
                task.start()
            }
            BiliCleaner.start()

            BiliTemplate.selenium() && SetupSelenium
        }

        if (BiliTemplate.selenium()) {
            BiliSeleniumConfig.reload()
            BiliSeleniumConfig.save()
        }
    }

    private fun waitOnline(block: () -> Unit) {
        if (Bot.instances.isEmpty()) {
            globalEventChannel().subscribeOnce<BotOnlineEvent> { block() }
        } else {
            block()
        }
    }

    override fun onDisable() {
        for (command in BiliHelperCommand) {
            command.unregister()
        }

        BiliListener.cancelAll()

        for (task in BiliTasker) {
            task.stop()
        }
        BiliCleaner.stop()
        client.save()
    }
}