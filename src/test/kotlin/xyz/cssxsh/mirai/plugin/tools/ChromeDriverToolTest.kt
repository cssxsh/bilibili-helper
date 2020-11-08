package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ChromeDriverToolTest {

    private val tool = ChromeDriverTool(
        driverPath = "D:\\Users\\CSSXSH\\IdeaProjects\\bilibili-helper\\test\\chromedriver.exe",
        deviceName = "iPad"
    )

    @AfterAll
    fun tearDown(): Unit = runBlocking {
        tool.close()
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