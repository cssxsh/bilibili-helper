package xyz.cssxsh.mirai.plugin.tools

import kotlinx.serialization.Serializable
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.ProtocolHandshake
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

class SeleniumTool(private val remote: URL, type: DriverType, deviceName: String = "") {
    companion object {
        @Suppress("unused")
        fun setLogLevel(level: Level) {
            ProtocolHandshake::class.java.getDeclaredField("LOG").apply {
                isAccessible = true
                (get(null) as Logger).level = level
            }
        }

        init {
            setLogLevel(Level.OFF)
        }

        internal fun loadJavaScript(name: String) = this::class.java.getResourceAsStream("$name.js")!!.use {
            it.reader().readText() + "\nreturn $name();"
        }
    }

    @Serializable
    enum class DriverType {
        CHROME,
        FIREFOX;
    }

    private val options: MutableCapabilities = when (type) {
        DriverType.CHROME -> ChromeOptions().apply {
            setHeadless(true)
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setExperimentalOption(
                "excludeSwitches",
                listOf("enable-automation", "ignore-certificate-errors")
            )
            if (deviceName.isNotBlank()) {
                setExperimentalOption(
                    "mobileEmulation",
                    mapOf("deviceName" to deviceName)
                )
            }
        }
        DriverType.FIREFOX -> FirefoxOptions().apply {
            setHeadless(true)
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setAcceptInsecureCerts(true)
            if (deviceName.isNotBlank()) {
                setCapability(
                    FirefoxOptions.FIREFOX_OPTIONS,
                    mapOf("mobileEmulation" to mapOf("deviceName" to deviceName))
                )
            }
        }
    }

    suspend fun <R> useDriver(block: suspend (RemoteWebDriver) -> R) = RemoteWebDriver(remote, options).let { driver ->
        driver.manage().timeouts().apply {
            pageLoadTimeout(10L, TimeUnit.MINUTES)
            setScriptTimeout(10L, TimeUnit.SECONDS)
        }
        runCatching {
            block(driver)
        }.also {
            driver.quit()
        }.getOrThrow()
    }
}