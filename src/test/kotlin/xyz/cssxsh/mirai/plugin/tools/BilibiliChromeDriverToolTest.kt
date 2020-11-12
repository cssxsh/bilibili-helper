package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.chrome.ChromeDriverService
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BilibiliChromeDriverToolTest {

    private val service : ChromeDriverService = ChromeDriverService.Builder().apply {
            usingDriverExecutable(File("test","chromedriver.exe"))
            withVerbose(false)
            withSilent(true)
            withWhitelistedIps("")
            usingPort(9515)
    }.build().apply { start() }

    private val tool = BilibiliChromeDriverTool(
        remoteAddress = service.url,
        deviceName = "iPad"
    )

    @AfterAll
    fun tearDown(): Unit = runBlocking {
        service.stop()
    }

    @Test
    fun getScreenShot(): Unit = runBlocking {
        tool.getScreenShot("https://t.bilibili.com/450055453856015371").let {
            File("test","450055453856015371.png").writeBytes(it)
        }
        tool.getScreenShot("https://t.bilibili.com/455352437023507650").let {
            File("test","455352437023507650.png").writeBytes(it)
        }
    }
}