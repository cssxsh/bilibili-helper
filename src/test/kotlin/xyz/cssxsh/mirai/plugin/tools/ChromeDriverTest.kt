package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class ChromeDriverTest {

    private val tool = ChromeDriverTool(
        driverPath = "D:\\Users\\CSSXSH\\IdeaProjects\\bilibili-helper\\test\\chromedriver.exe",
        chromePath = null,
        deviceName = "iPad"
    )

    @Test
    fun getScreenShot(): Unit = runBlocking {
        tool.getScreenShot("https://t.bilibili.com/450055453856015371", 1_000)
        tool.getScreenShot("https://t.bilibili.com/450055453856015372", 1_000)
    }
}