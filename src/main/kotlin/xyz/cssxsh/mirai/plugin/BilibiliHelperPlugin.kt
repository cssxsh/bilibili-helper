package xyz.cssxsh.mirai.plugin

import com.google.auto.service.AutoService
import kotlinx.coroutines.Job
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import xyz.cssxsh.mirai.plugin.command.BiliBiliSubscribeCommand
import xyz.cssxsh.mirai.plugin.data.*
import net.mamoe.mirai.utils.warning
import org.openqa.selenium.chrome.ChromeDriverService
import xyz.cssxsh.bilibili.BilibiliClient
import xyz.cssxsh.mirai.plugin.command.BilibiliInfoCommand
import xyz.cssxsh.mirai.plugin.data.BilibiliChromeDriverConfig.driverPath
import xyz.cssxsh.mirai.plugin.data.BilibiliHelperSettings.initCookies
import java.io.File
import kotlin.time.minutes

@AutoService(JvmPlugin::class)
object BilibiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "0.1.0-dev-1") {
        name("bilibili-helper")
        author("cssxsh")
    }
) {

    internal lateinit var bilibiliClient : BilibiliClient
        private set

    private var service: ChromeDriverService? = null

    private fun serviceStart() {
        if (driverPath.isNotBlank()) {
            runCatching {
                service = ChromeDriverService.Builder().apply {
                    usingDriverExecutable(File(driverPath))
                    withVerbose(false)
                    withSilent(true)
                    withWhitelistedIps("")
                    usingPort(9515)
                }.build().apply { start() }
            }.onFailure {
                logger.warning({ "启动${driverPath}失败" }, it)
            }
        }
    }

    private fun serviceStop() {
        service?.stop()
    }

    private lateinit var bilibiliInfoJob: Job

    @ConsoleExperimentalApi
    override val autoSaveIntervalMillis: LongRange
        get() = (3).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds()

    @ConsoleExperimentalApi
    override fun onEnable() {
        BiliBiliSubscribeCommand.onInit()
        BilibiliTaskData.reload()
        BilibiliChromeDriverConfig.reload()
        BilibiliHelperSettings.reload()
        BiliBiliSubscribeCommand.register()
        BilibiliInfoCommand.register()

        bilibiliClient = BilibiliClient(initCookies)
        BilibiliHelperSettings.cachePath.let { File(it).mkdirs() }
        bilibiliInfoJob = BilibiliInfoCommand.subscribeBilibiliInfo()
        serviceStart()
    }

    @ConsoleExperimentalApi
    override fun onDisable() {
        bilibiliInfoJob.cancel()
        serviceStop()

        BiliBiliSubscribeCommand.unregister()
        BilibiliInfoCommand.unregister()
    }
}