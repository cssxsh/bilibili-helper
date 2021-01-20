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
import net.mamoe.mirai.message.data.buildMessageChain
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData.tasks
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.minutes

@ConsoleExperimentalApi
@ConsoleTerminalExperimentalApi
object RunMirai {

    private fun miraiConsoleImpl(rootPath: Path) = MiraiConsoleImplementationTerminal(
        rootPath = rootPath,
        dataStorageForJvmPluginLoader = JsonPluginDataStorage(rootPath.resolve("data"), false),
        dataStorageForBuiltIns = JsonPluginDataStorage(rootPath.resolve("data"), false),
        configStorageForJvmPluginLoader = JsonPluginDataStorage(rootPath.resolve("config"), true),
        configStorageForBuiltIns = JsonPluginDataStorage(rootPath.resolve("config"), true),
    )

    private fun getAuthorOrNull() =  Bot.instances.map { it.getFriend(1438159989L) }.firstOrNull()

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // 默认在 /test 目录下运行
        MiraiConsoleTerminalLoader.parse(args = args, exitProcess = true)
        MiraiConsoleTerminalLoader.startAsDaemon(miraiConsoleImpl(Paths.get(".").toAbsolutePath()))
        MiraiConsole.apply {
            StateCommand.register()
            launch {
                while (isActive) {
                    getAuthorOrNull()?.runCatching {
                        sendMessage(buildMessageChain {
                            appendLine("存活！")
                            tasks.toMap().forEach { (uid, info) ->
                                appendLine("$uid -> Friends: ${info.friends}, Groups: ${info.groups}")
                            }
                        })
                    }
                    delay((10).minutes)
                }
            }
        }
        try {
            MiraiConsole.job.join()
        } catch (e: CancellationException) {
            // ignored
        }
    }
}