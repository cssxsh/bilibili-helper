package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.chrome.ChromeDriverService
import java.io.File
import java.net.URL

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BilibiliChromeDriverToolTest {

    private val service : ChromeDriverService = ChromeDriverService.Builder().apply {
            usingDriverExecutable(File("test","chromedriver.exe"))
            withVerbose(false)
            withSilent(true)
            withWhitelistedIps("")
            usingPort(9515)
    }.build()

    private val tool = BilibiliChromeDriverTool(
        remoteAddress = URL("http://127.0.0.1:9515"),
        deviceName = "iPad"
    )

    private val dynamicIds = listOf(
        450055453856015371,
        456805261245236351
    )

    @BeforeAll
    fun setUp(): Unit = runBlocking {
        // service.start()
    }

    @AfterAll
    fun tearDown(): Unit = runBlocking {
        // service.stop()
    }

    @Test
    fun getScreenShot(): Unit = runBlocking {
        dynamicIds.forEach { id ->
            tool.getScreenShot(url = "https://t.bilibili.com/h5/dynamic/detail/${id}").let {
                File("test","${id}.png").writeBytes(it)
            }
        }
    }
}