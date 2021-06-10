package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SeleniumToolTest {
    private val dir = File("./test")

    init {
        setupSelenium(dir)
    }

    private val list = listOf(
        450055453856015371,
        456805261245236351,
        468989129984490798
    )

    private fun dynamic(id: Long) = "https://t.bilibili.com/h5/dynamic/detail/$id"

    @Test
    fun getScreenShot(): Unit = runBlocking {
        val driver = RemoteWebDriver()
        driver.get("https://t.bilibili.com/h5/dynamic/detail/508396365455813655")
        list.forEach { id ->
            driver.getScreenshot(dynamic(id)).let {
                dir.resolve("${id}.png").writeBytes(it)
            }
        }
        driver.quit()
    }
}