package xyz.cssxsh.mirai.plugin

import com.google.auto.service.AutoService
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import xyz.cssxsh.mirai.plugin.command.BiliBiliCommand
import xyz.cssxsh.mirai.plugin.data.*
import net.mamoe.mirai.utils.minutesToMillis
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.chromePath
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.deviceName
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.driverPath
import xyz.cssxsh.mirai.plugin.tools.BilibiliChromeDriverTool

@AutoService(JvmPlugin::class)
object BilibiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "0.1.0-dev-1") {
        name("bilibili-helper")
        author("cssxsh")
    }
)  {

    var driverTool: BilibiliChromeDriverTool? = null
        private set

    @ConsoleExperimentalApi
    override val autoSaveIntervalMillis: LongRange
        get() = 3.minutesToMillis..10.minutesToMillis

    @ConsoleExperimentalApi
    override fun onEnable() {
        BilibiliTaskData.reload()
        BilibiliChromeDriverConfig.reload()
        BiliBiliCommand.register()
        BiliBiliCommand.onInit()

        driverTool = driverPath.takeIf { it.isNotBlank() }?.let {
            BilibiliChromeDriverTool(it, chromePath, deviceName)
        }
    }

    @ConsoleExperimentalApi
    override fun onDisable() {
        BiliBiliCommand.unregister()

        driverTool?.close()
    }
}