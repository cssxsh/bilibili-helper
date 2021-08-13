package xyz.cssxsh.mirai.plugin.tools

import io.github.karlatemp.mxlib.*
import io.github.karlatemp.mxlib.logger.*
import io.github.karlatemp.mxlib.selenium.*
import kotlinx.coroutines.*
import org.openqa.selenium.*
import org.openqa.selenium.chromium.*
import org.openqa.selenium.firefox.*
import org.openqa.selenium.interactions.*
import org.openqa.selenium.remote.*
import java.io.*
import java.time.*
import java.util.logging.*

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

interface RemoteWebDriverConfig {
    val userAgent: String
    val width: Int
    val height: Int
    val pixelRatio: Int
}

typealias DriverConsumer = (Capabilities) -> Unit

private fun RemoteWebDriverConfig.toConsumer(): DriverConsumer = { capabilities ->
    when (capabilities) {
        is ChromiumOptions<*> -> capabilities.apply {
            //setHeadless(true)
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setAcceptInsecureCerts(true)
            addArguments("--silent")
            setExperimentalOption(
                "excludeSwitches",
                listOf("enable-automation", "ignore-certificate-errors")
            )
            addArguments("--hide-scrollbars")
            setExperimentalOption(
                "mobileEmulation",
                mapOf(
                    "deviceMetrics" to mapOf(
                        "width" to width,
                        "height" to height,
                        "pixelRatio" to pixelRatio
                    ),
                    "userAgent" to userAgent
                )
            )
        }
        is FirefoxOptions -> capabilities.apply {
            setHeadless(true)
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setLogLevel(FirefoxDriverLogLevel.FATAL)
            setAcceptInsecureCerts(true)
            // XXX 手动关闭 webgl
            addPreference("webgl.disabled", true)
            addPreference("devtools.responsive.touchSimulation.enabled", true)
            addPreference("devtools.responsive.viewport.width", width)
            addPreference("devtools.responsive.viewport.height", height)
            addPreference("devtools.responsive.viewport.pixelRatio", pixelRatio)
            addPreference("devtools.responsive.userAgent", userAgent)
            // XXX responsive 无法调用
            addPreference("general.useragent.override", userAgent)
            addArguments("--width=${width}", "--height=${height}")
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

fun RemoteWebDriver(config: RemoteWebDriverConfig, home: String = HOME_PAGE): RemoteWebDriver {

    val thread = Thread.currentThread()
    val oc = thread.contextClassLoader

    val driver = runCatching {
        thread.contextClassLoader = KtorHttpClientFactory::class.java.classLoader

        MxSelenium.newDriver(null, config.toConsumer()).apply {
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

private fun WebDriver.responsive() {
    val actions = Actions(this)
    actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("m").keyUp(Keys.SHIFT).keyUp(Keys.CONTROL).perform()
}

suspend fun RemoteWebDriver.getScreenshot(url: String): ByteArray {
    val home = windowHandle
    val tab = switchTo().newWindow(WindowType.TAB) as RemoteWebDriver
    tab.get(url)
    // tab.responsive()
    runCatching {
        withTimeout(Timeout.toMillis()) {
            delay(Init.toMillis())
            while (executeScript(IS_READY_SCRIPT) == false || executeScript(HAS_CONTENT_SCRIPT) == false) {
                delay(Interval.toMillis())
            }
        }
    }
    val bytes = getScreenshotAs(OutputType.BYTES)
    tab.close()
    switchTo().window(home)
    return bytes
}