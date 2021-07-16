package xyz.cssxsh.mirai.plugin.tools

import io.github.karlatemp.mxlib.MxLib
import io.github.karlatemp.mxlib.logger.NopLogger
import io.github.karlatemp.mxlib.selenium.MxSelenium
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.openqa.selenium.Capabilities
import org.openqa.selenium.OutputType
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WindowType
import org.openqa.selenium.chromium.ChromiumOptions
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.ProtocolHandshake
import org.openqa.selenium.remote.RemoteWebDriver
import xyz.cssxsh.mirai.plugin.*
import java.io.File
import java.time.Duration
import java.util.logging.Level
import java.util.logging.Logger

private fun Class<*>.getLogger(): Logger {
    return declaredFields.first { it.type == Logger::class.java }.apply { isAccessible = true }.get(null) as Logger
}

internal fun setupSelenium(dir: File) {
    System.setProperty("webdriver.http.factory", "ktor")
    System.setProperty("io.ktor.random.secure.random.provider", "DRBG")
    MxLib.setLoggerFactory { name -> NopLogger(name) }
    MxLib.setDataStorage(dir)
    ProtocolHandshake::class.java.getLogger().parent.level = Level.OFF

    val thread = Thread.currentThread()
    val oc = thread.contextClassLoader
    try {
        thread.contextClassLoader = KtorHttpClientFactory::class.java.classLoader
        MxSelenium.initialize()
    } finally {
        thread.contextClassLoader = oc
    }
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

private val Init = Duration.ofSeconds(10)

private val Timeout = Duration.ofMinutes(3)

private val Interval = Duration.ofSeconds(3)

private const val HOME_PAGE = "https://t.bilibili.com/h5/dynamic/detail/508396365455813655"

fun RemoteWebDriver(home: String = HOME_PAGE): RemoteWebDriver {

    val thread = Thread.currentThread()
    val oc = thread.contextClassLoader

    val driver = runCatching {
        thread.contextClassLoader = KtorHttpClientFactory::class.java.classLoader

        MxSelenium.newDriver(null, DriverConsumer).apply {
            // 诡异的等级
            setLogLevel(Level.ALL)
            manage().timeouts().apply {
                pageLoadTimeout(Timeout)
                scriptTimeout = Interval
            }
            get(home)
        }
    }

    thread.contextClassLoader = oc

    return driver.getOrThrow()
}

suspend fun RemoteWebDriver.getScreenshot(url: String): ByteArray {
    val pre = windowHandle
    val new = switchTo().newWindow(WindowType.TAB)
    new.get(url)
    runCatching {
        withTimeout(Timeout.toMillis()) {
            delay(Init.toMillis())
            while (executeScript(IS_READY_SCRIPT) == false || executeScript(HAS_CONTENT_SCRIPT) == false) {
                delay(Interval.toMillis())
            }
        }
    }
    val bytes = getScreenshotAs(OutputType.BYTES)
    new.close()
    switchTo().window(pre)
    return bytes
}