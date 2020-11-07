package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ScreenShotToolTest {

    private val tool = ScreenShotTool(
        driverPath = "D:\\Users\\CSSXSH\\IdeaProjects\\bilibili-helper\\test\\chromedriver.exe",
        chromePath = null,
        deviceName = "iPad"
    )

    @Test
    fun getScreenShot(): Unit = runBlocking {
        tool.getScreenShot("https://t.bilibili.com/450055453856015371", 1_000)
    }
}