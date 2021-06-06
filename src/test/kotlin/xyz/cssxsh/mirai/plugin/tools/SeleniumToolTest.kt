package xyz.cssxsh.mirai.plugin.tools

import io.github.karlatemp.mxlib.MxLib
import io.github.karlatemp.mxlib.logger.NopLogger
import io.github.karlatemp.mxlib.selenium.MxSelenium
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.remote.http.HttpClient
import java.io.File
import java.util.logging.Level

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SeleniumToolTest {
    private val dir = File("./test")

    init {
        MxLib.setLoggerFactory { name -> NopLogger(name) }
        setSeleniumLogLevel(Level.ALL)
        MxLib.setDataStorage(dir)
        // System.setProperty("webdriver.http.factory", "ktor")
    }

    private val list = listOf(
        450055453856015371,
        456805261245236351,
        468989129984490798
    )

    private fun dynamic(id: Long) = "https://t.bilibili.com/h5/dynamic/detail/$id"

    @Test
    fun getScreenShot(): Unit = runBlocking {
        val driver = MxSelenium.newDriver(null, DriverConsumer)
        driver.get("https://t.bilibili.com/h5/dynamic/detail/508396365455813655")
        list.forEach { id ->
            driver.getScreenshot(dynamic(id)).let {
                dir.resolve("${id}.png").writeBytes(it)
            }
        }
        driver.quit()
    }
}