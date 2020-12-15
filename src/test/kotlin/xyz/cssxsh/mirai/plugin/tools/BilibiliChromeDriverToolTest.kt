package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xyz.cssxsh.mirai.plugin.DYNAMIC_DETAIL
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

    @Test
    fun getScreenShot(): Unit = runBlocking {
        tool.useDriver { driver ->
            driver.getScreenShot("https://www.bilibili.com/").let {
                println(it.size)
                File("test","index.png").writeBytes(it)
            }
            dynamicIds.forEach { id ->
                driver.getScreenShot(url = DYNAMIC_DETAIL + id).let {
                    println(it.size)
                    File("test","${id}.png").writeBytes(it)
                }
            }
        }
    }
}