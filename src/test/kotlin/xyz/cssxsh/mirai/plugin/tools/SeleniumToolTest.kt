package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import java.io.*

internal class SeleniumToolTest {
    private val dir = File("test")

    init {
        // System.setProperty("mxlib.selenium.browser", "AppX90nv6nhay5n6a98fnetv7tpk64pp35es");
        // System.setProperty("webdriver.edge.driver", dir.resolve("msedgedriver.exe").absolutePath);
        setupSelenium(dir)
    }

    private val list = listOf(
        450055453856015371,
        456805261245236351,
        468989129984490798
    )

    private fun dynamic(id: Long) = "https://t.bilibili.com/h5/dynamic/detail/$id"

    private val config = object : RemoteWebDriverConfig {
        override val userAgent: String = UserAgents.MAC
        override val width: Int = 768
        override val height: Int = 1024
        override val pixelRatio: Int = 3
        override val headless: Boolean = false
        override val browser: String = ""
    }

    @Test
    fun home(): Unit = runBlocking {
        val driver = RemoteWebDriver(config = config)
        val version = driver.home()
        println(version)
        driver.quit()
    }

    @Test
    fun getScreenShot(): Unit = runBlocking {
       val driver = RemoteWebDriver(config = config)
        list.forEach { id ->
            dir.resolve("${id}.png").writeBytes(driver.getScreenshot(url = dynamic(id)))
        }
        driver.quit()
    }
}