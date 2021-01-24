package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.net.URL

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BilibiliChromeDriverToolTest {

    private val tool = BilibiliChromeDriverTool(
        remoteAddress = URL("http://10.70.159.64:9515"),
        deviceName = "iPad"
    )

    private val dynamicIds = listOf(
        450055453856015371,
        456805261245236351,
        468989129984490798
    )

    private fun getDynamicUrl(id: Long) = "https://t.bilibili.com/h5/dynamic/detail/$id"

    @Test
    fun getScreenShot(): Unit = runBlocking {
        dynamicIds.forEach { id ->
            tool.useDriver { driver ->
                driver.getScreenShot(url = getDynamicUrl(id)).let {
                    println("${id}: ${it.size}")
                    File("test", "${id}.png").writeBytes(it)
                }
            }
        }
    }
}