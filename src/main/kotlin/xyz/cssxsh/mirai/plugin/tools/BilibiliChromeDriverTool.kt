package xyz.cssxsh.mirai.plugin.tools

import kotlinx.io.core.Closeable
import org.openqa.selenium.OutputType
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.ProtocolHandshake
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.io.File
import java.time.Duration
import java.util.logging.Level
import java.util.logging.Logger

class BilibiliChromeDriverTool(
    private val driverPath: String,
    chromePath: String = "",
    deviceName: String = ""
) : Closeable {

    companion object {
        init {
            ProtocolHandshake::class.java.getDeclaredField("LOG").apply {
                isAccessible = true
                (get(null) as Logger).level = Level.OFF
            }
        }

        val LOADED_SCRIPT = """
            function findVue() {
                let Vue = null;
                document.body.children.forEach((element) => {
                    Vue = Vue || element.__vue__
                })
                return Vue
            };
            function isReady() {
                return document.readyState === 'complete' && findVue()._isMounted
            };
            return isReady();
        """.trimIndent()
    }

    private val service = ChromeDriverService.Builder().apply {
        usingDriverExecutable(File(driverPath))
        withVerbose(false)
        withSilent(true)
    }.build().apply { start() }

    private val options = ChromeOptions().apply {
        setHeadless(true)
        setPageLoadStrategy(PageLoadStrategy.NORMAL)
        if (chromePath.isNotBlank()) {
            setBinary(chromePath)
        }
        if (deviceName.isNotBlank()) {
            setExperimentalOption("mobileEmulation", mapOf("deviceName" to deviceName))
        }
    }

    fun <R> useWait(driver: RemoteWebDriver, timeoutMillis: Long, block: (RemoteWebDriver) -> R) = FluentWait(driver)
        .withTimeout(Duration.ofMillis(timeoutMillis)).until(block)

    fun <R> useDriver(block: (RemoteWebDriver) -> R) = RemoteWebDriver(remoteAddress, options).let { driver ->
        block(driver).also { driver.quit() }
    }

    fun getScreenShot(url: String, timeoutMillis: Long = 30_000): ByteArray = useDriver { driver ->
        driver.get(url)
        useWait(driver, timeoutMillis) {
            it.executeScript(LOADED_SCRIPT)
        }
        driver.getScreenshotAs(OutputType.BYTES)
    }

    override fun close() {
        service.stop()
    }
}