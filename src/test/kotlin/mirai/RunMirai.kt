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
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsVoice
import java.io.File
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
                    OffsetDateTime.now().plusMinutes(30).run {
                        withMinute(if (minute < 30) 0 else 30).withSecond(0)
                    }.toEpochSecond().let { after ->
                        delay((after - OffsetDateTime.now().toEpochSecond()).seconds)
                    }
                }
            }
            GlobalEventChannel.parentScope(this).subscribeAlways<MemberJoinEvent> {
                group.runCatching {
                    sendMessage(buildMessageChain {
                        appendLine("欢迎新人")
                        appendLine("新人请注意群公告，以免被踢")
                        append(At(member))
                    })
                    File("./welcome.amr").takeIf { it.exists() }?.run {
                        sendMessage(toExternalResource().uploadAsVoice(group))
                    }
                }
            }
            GlobalEventChannel.parentScope(this).subscribeGroupMessages {
                """来点[Dd]ebu""".toRegex() findingReply {
                    File("./debu/").listFiles()?.randomOrNull()?.run {
                        println("播放文件${name}")
                        toExternalResource().uploadAsVoice(group)
                    }
                }
                """来点[Hh]iiro""".toRegex() findingReply {
                    File("./hiiro/").listFiles()?.randomOrNull()?.run {
                        println("播放文件${name}")
                        toExternalResource().uploadAsVoice(group)
                    }
                }
                """来点[Tt]est""".toRegex() findingReply {
                    File("./test/").listFiles()?.randomOrNull()?.run {
                        println("播放文件${name}")
                        toExternalResource().uploadAsVoice(group)
                    }
                }
                """来点群友""".toRegex() findingReply {
                    File("./anti/").listFiles()?.randomOrNull()?.run {
                        println("播放文件${name}")
                        toExternalResource().uploadAsVoice(group)
                    }
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