package mirai

import kotlinx.coroutines.*
import mirai.command.StateCommand
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.terminal.ConsoleTerminalExperimentalApi
import net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.utils.minutesToMillis
import org.openqa.selenium.chrome.ChromeDriverService
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.tasks
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

@ConsoleExperimentalApi
@ConsoleTerminalExperimentalApi
object RunMirai {

    private val service : ChromeDriverService? = runCatching {
        ChromeDriverService.Builder().apply {
            usingDriverExecutable(File("chromedriver.exe"))
            withVerbose(false)
            withSilent(true)
            withWhitelistedIps("")
            usingPort(9515)
        }.build()
    }.getOrNull()

    private fun miraiConsoleImpl(rootPath: Path) = MiraiConsoleImplementationTerminal(
        rootPath = rootPath,
        dataStorageForJvmPluginLoader = JsonPluginDataStorage(rootPath.resolve("data"), false),
        dataStorageForBuiltIns = JsonPluginDataStorage(rootPath.resolve("data"), false),
        configStorageForJvmPluginLoader = JsonPluginDataStorage(rootPath.resolve("config"), true),
        configStorageForBuiltIns = JsonPluginDataStorage(rootPath.resolve("config"), true),
    )

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // 默认在 /test 目录下运行
        MiraiConsoleTerminalLoader.parse(args, exitProcess = true)
        MiraiConsoleTerminalLoader.startAsDaemon(miraiConsoleImpl(Paths.get(".").toAbsolutePath()))
        MiraiConsole.apply {
            StateCommand.register()
            val block: (Friend) -> Job = { friend ->
                friend.launch {
                    while (isActive) {
                        friend.sendMessage(buildString {
                            appendLine("存活！")
                            tasks.toMap().forEach { (uid, info) ->
                                appendLine("$uid -> Friends: ${info.friends}, Groups: ${info.groups}")
                            }
                        })
                        delay((10).minutesToMillis)
                    }
                }
            }
            Bot.botInstances.forEach { bot -> bot.friends.getOrNull(1438159989L)?.let(block) }
            subscribeAlways<BotOnlineEvent> { bot.friends.getOrNull(1438159989L)?.let(block) }
        }
        try {
            service?.start()
            MiraiConsole.job.join()
        } catch (e: CancellationException) {
            // ignored
        }
        service?.stop()
    }
}