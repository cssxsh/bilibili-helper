package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.*
import org.openqa.selenium.Capabilities
import org.openqa.selenium.OutputType
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WindowType
import org.openqa.selenium.chromium.ChromiumOptions
import org.openqa.selenium.devtools.CdpVersionFinder
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.ProtocolHandshake
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.http.WebSocket
import org.openqa.selenium.remote.service.DriverService
import xyz.cssxsh.mirai.plugin.DeviceName
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.time.Duration
import kotlin.time.minutes
import kotlin.time.seconds
import kotlin.time.toJavaDuration

private fun Class<*>.getLogger(): Logger {
    return declaredFields.first { it.type == Logger::class.java }.apply { isAccessible = true }.get(null) as Logger
}

internal fun setSeleniumLogLevel(level: Level) {
    CdpVersionFinder::class.java.getLogger().level = level
    ProtocolHandshake::class.java.getLogger().level = level
    WebSocket::class.java.getLogger().level = level
}

private object JavaScriptLoader {
    fun load(name: String): String {
        return requireNotNull(this::class.java.getResourceAsStream("$name.js")) { "脚本${name}不存在" }.use {
            it.reader().readText() + "\nreturn $name();"
        }
    }
}

internal val DriverConsumer: (Capabilities) -> Unit = { capabilities ->
    when (capabilities) {
        is ChromiumOptions<*> -> capabilities.apply {
            setHeadless(true)
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setAcceptInsecureCerts(true)
            addArguments("--silent")
            setExperimentalOption(
                "excludeSwitches",
                listOf("enable-automation", "ignore-certificate-errors")
            )
            if (DeviceName.isNotBlank()) {
                setExperimentalOption("mobileEmulation", mapOf("deviceName" to DeviceName))
            }
        }
        is FirefoxOptions -> capabilities.apply {
            setHeadless(true)
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setLogLevel(FirefoxDriverLogLevel.FATAL)
            setAcceptInsecureCerts(true)
            if (DeviceName.isNotBlank()) {
                setCapability(
                    FirefoxOptions.FIREFOX_OPTIONS,
                    mapOf("mobileEmulation" to mapOf("deviceName" to DeviceName))
                )
            }
        }
        else -> throw IllegalArgumentException("未设置参数的浏览器")
    }
}

private val IS_READY_SCRIPT by lazy { JavaScriptLoader.load("IsReady") }

private val HAS_CONTENT_SCRIPT by lazy { JavaScriptLoader.load("HasContent") }

private val Init = (10).seconds

private val Timeout = (3).minutes

private val Interval = (3).seconds

private const val HOME_PAGE = "https://t.bilibili.com/h5/dynamic/detail/508396365455813655"

fun RemoteWebDriver.init() = apply {
    // 诡异的等级
    setLogLevel(Level.ALL)
    manage().timeouts().apply {
        pageLoadTimeout(Timeout.toJavaDuration())
        setScriptTimeout(Interval.toJavaDuration())
    }
    get(HOME_PAGE)
}

suspend fun RemoteWebDriver.getScreenshot(url: String): ByteArray {
    val pre = windowHandle
    val new = switchTo().newWindow(WindowType.TAB)
    new.get(url)
    runCatching {
        withTimeout(Timeout) {
            delay(Init)
            while (executeScript(IS_READY_SCRIPT) == false || executeScript(HAS_CONTENT_SCRIPT) == false) {
                delay(Interval)
            }
        }
    }
    val bytes = getScreenshotAs(OutputType.BYTES)
    new.close()
    switchTo().window(pre)
    return bytes
}