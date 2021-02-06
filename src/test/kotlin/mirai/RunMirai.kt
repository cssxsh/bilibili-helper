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
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import java.nio.file.Path
import java.nio.file.Paths
import java.time.OffsetDateTime
import kotlin.time.minutes
import kotlin.time.seconds

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

    private fun getTestGroupOrNull() =  Bot.instances.map { it.getGroup(983400877L) }.firstOrNull()

    @JvmStatic
    fun main(args: Array<String>): Unit = runBlocking {
        // 默认在 /test 目录下运行
        MiraiConsoleTerminalLoader.parse(args = args, exitProcess = true)
        MiraiConsoleTerminalLoader.startAsDaemon(miraiConsoleImpl(Paths.get(".").toAbsolutePath()))
        MiraiConsole.apply {
            StateCommand.register()
            // sign
            launch {
                while (isActive) {
                    getAuthorOrNull()?.runCatching {
                        sendMessage(buildMessageChain {
                            appendLine("存活！")
                        })
                    }
                    delay((10).minutes)
                }
            }
            // datetime
            launch {
                while (isActive) {
                    OffsetDateTime.now().takeIf {
                        it.minute % 30 == 0
                    }?.let { datetime ->
                        getTestGroupOrNull()?.runCatching {
                            sendMessage(buildMessageChain {
                                appendLine("定点报时, 现在：${datetime}")
                            })
                        }
                    }
                    OffsetDateTime.now().run {
                        plusMinutes(30).withMinute(if (minute < 30) 0 else 30).withSecond(0)
                    }.toEpochSecond().let { after ->
                        delay((after - OffsetDateTime.now().toEpochSecond()).seconds)
                    }
                }
            }
            GlobalEventChannel.parentScope(this).subscribeAlways<MemberJoinEvent> {
                group.sendMessage(buildMessageChain {
                    appendLine("欢迎新人")
                    append(At(member))
                    appendLine("新人请注意群公告，以免被踢")
                })
            }
        }
        try {
            MiraiConsole.job.join()
        } catch (e: CancellationException) {
            // ignored
        }
    }
}