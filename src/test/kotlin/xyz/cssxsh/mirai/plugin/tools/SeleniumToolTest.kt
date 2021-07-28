package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SeleniumToolTest {
    private val dir = File("./test")

    init {
        // System.setProperty("mxlib.selenium.browser", "firefox")
        setupSelenium(dir)
    }

    private val list = listOf(
        450055453856015371,
        456805261245236351,
        468989129984490798
    )

    private fun dynamic(id: Long) = "https://t.bilibili.com/$id"

    private val ipad = "Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1"

    @Test
    fun getScreenShot(): Unit = runBlocking {
        val driver = RemoteWebDriver(ua = ipad, width = 768, height = 1024)
        driver.get("https://t.bilibili.com/h5/dynamic/detail/508396365455813655")
        list.forEach { id ->
            driver.getScreenshot(url = dynamic(id)).let {
                dir.resolve("${id}.png").writeBytes(it)
            }
        }
        driver.quit()
    }
}