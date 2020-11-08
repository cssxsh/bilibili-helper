package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.delay
import kotlinx.io.core.Closeable
import org.openqa.selenium.OutputType
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.io.File

class ChromeDriverTool(
    private val driverPath: String,
    chromePath: String? = null,
    deviceName: String? = null
): Closeable {

    private val service = ChromeDriverService.Builder().apply {
        usingDriverExecutable(File(driverPath))
        withVerbose(false)
        withSilent(true)
    }.build().apply {
        // sendOutputTo()
        start()
    }

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
        setExperimentalOption("excludeSwitches", listOf("enable-logging"))
    }

    suspend fun getScreenShot(
        url: String,
        delayMillis: Long = 10_000
    ): ByteArray = RemoteWebDriver(service.url, options).run {
        manage().window()
        get(url)
        delay(delayMillis)
        getScreenshotAs(OutputType.BYTES).also {
            quit()
        }
    }

    override fun close() {
        service.stop()
    }
}