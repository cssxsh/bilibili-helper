package xyz.cssxsh.mirai.plugin

import com.google.auto.service.AutoService
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import xyz.cssxsh.mirai.plugin.command.BiliBiliCommand
import xyz.cssxsh.mirai.plugin.data.BiliAccInfo
import xyz.cssxsh.mirai.plugin.data.BiliRoomInfo
import xyz.cssxsh.mirai.plugin.data.BiliSearchResult
import xyz.cssxsh.mirai.plugin.data.BilibiliTaskData

@AutoService(JvmPlugin::class)
object BilibiliHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.bilibili-helper", "0.1.0-dev-1") {
        name("bilibili-helper")
        author("cssxsh")
    }
)  {
    private const val SEARCH_URL = "https://api.bilibili.com/x/space/arc/search"
    private const val ROOM_INIT = "http://api.live.bilibili.com/room/v1/Room/room_init"
    private const val ACC_INFO = "https://api.bilibili.com/x/space/acc/info"

    private suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            requestTimeoutMillis = 180_000
        }
        BrowserUserAgent()
        ContentEncoding {
            gzip()
            deflate()
            identity()
        }
    }.use { block(it) }

    suspend fun searchVideo(
        uid: Long,
        pageSize: Int = 30,
        pageNum: Int = 1
    ): BiliSearchResult = useHttpClient { client ->
        client.get(SEARCH_URL) {
            parameter("mid", uid)
            parameter("keyword", "")
            parameter("order", "pubdate")
            parameter("jsonp", "jsonp")
            parameter("ps", pageSize)
            parameter("pn", pageNum)
            parameter("tid", 0)
        }
    }

    suspend fun accInfo(
        uid: Long
    ): BiliAccInfo = useHttpClient { client ->
        client.get(ACC_INFO) {
            parameter("mid", uid)
            parameter("jsonp", "jsonp")
            parameter("tid", 0)
        }
    }

    suspend fun roomInfo(
        id: Long
    ): BiliRoomInfo = useHttpClient { client ->
        client.get(ROOM_INIT) {
            parameter("id", id)
        }
    }

    suspend fun getPic(url: String): ByteArray = useHttpClient { client ->
        client.get(url)
    }

    @ConsoleExperimentalApi
    override fun onEnable() {
        BilibiliTaskData.reload()
        BiliBiliCommand.onInit()
        BiliBiliCommand.register()
    }

    @ConsoleExperimentalApi
    override fun onDisable() {
        BiliBiliCommand.unregister()
    }
}