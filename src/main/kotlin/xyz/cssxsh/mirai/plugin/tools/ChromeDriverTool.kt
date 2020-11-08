package xyz.cssxsh.mirai.plugin.tools

import kotlinx.io.core.Closeable
import org.openqa.selenium.OutputType
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.io.File
import java.time.Duration

class ChromeDriverTool(
    private val driverPath: String,
    chromePath: String? = null,
    deviceName: String? = null
) : Closeable {

    private val service = ChromeDriverService.Builder().apply {
        usingDriverExecutable(File(driverPath))
        withVerbose(false)
        withSilent(true)
    }.build().apply { start() }

    private val options = ChromeOptions().apply {
        setHeadless(true)
        chromePath.takeUnless {
            it.isNullOrEmpty()
        }?.let {
            setBinary(it)
        }
        deviceName.takeUnless {
            it.isNullOrEmpty()
        }?.let {
            setExperimentalOption("mobileEmulation", mapOf("deviceName" to deviceName))
        }
    }

    fun getScreenShot(
        url: String,
        timeoutMillis: Long = 10_000
    ): ByteArray = FluentWait(RemoteWebDriver(service.url, options).apply { get(url) }).withTimeout(Duration.ofMillis(timeoutMillis)).until { driver ->
        driver.getScreenshotAs(OutputType.BYTES).also {
            driver.quit()
        }
    }

    override fun close() {
        service.stop()
    }
}