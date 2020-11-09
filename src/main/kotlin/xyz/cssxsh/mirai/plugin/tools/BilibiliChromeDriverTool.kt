package xyz.cssxsh.mirai.plugin.tools

import kotlinx.io.core.Closeable
import org.openqa.selenium.OutputType
import org.openqa.selenium.chrome.ChromeDriverService
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
    }

    private val service = ChromeDriverService.Builder().apply {
        usingDriverExecutable(File(driverPath))
        withVerbose(false)
        withSilent(true)
    }.build().apply { start() }

    private val options = ChromeOptions().apply {
        setHeadless(true)
        if (chromePath.isNotBlank()) {
            setBinary(chromePath)
        }
        if (deviceName.isNotBlank()) {
            setExperimentalOption("mobileEmulation", mapOf("deviceName" to deviceName))
        }
    }

    fun <R> useDriver(preBlock: (RemoteWebDriver) -> Unit, timeoutMillis: Long, block: (RemoteWebDriver) -> R) =
        FluentWait(RemoteWebDriver(service.url, options).also(preBlock)).withTimeout(Duration.ofMillis(timeoutMillis))
            .until { driver -> block(driver).also { driver.quit() } }

    fun <R> useDriver(url: String, timeoutMillis: Long, block: (RemoteWebDriver) -> R) =
        useDriver(preBlock = { driver -> driver.get(url) }, timeoutMillis = timeoutMillis, block = block)

    fun getScreenShot(url: String, timeoutMillis: Long = 10_000): ByteArray = useDriver(url, timeoutMillis) { driver ->
        driver.getScreenshotAs(OutputType.BYTES)
    }

    override fun close() {
        service.stop()
    }
}