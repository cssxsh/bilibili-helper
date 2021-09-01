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

internal fun setupSelenium(dir: File, browser: String = "") {
    if (browser.isNotBlank()) System.setProperty("mxlib.selenium.browser", browser)
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
    val headless: Boolean
    val browser: String
}

typealias DriverConsumer = (Capabilities) -> Unit

private fun RemoteWebDriverConfig.toConsumer(): DriverConsumer = { capabilities ->
    when (capabilities) {
        is ChromiumOptions<*> -> capabilities.apply {
            setHeadless(headless)
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
                    "userAgent" to "$userAgent MicroMessenger"
                )
            )
        }
        is FirefoxOptions -> capabilities.apply {
            setHeadless(headless)
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setLogLevel(FirefoxDriverLogLevel.FATAL)
            setAcceptInsecureCerts(true)
            // XXX 手动关闭 webgl
            addPreference("webgl.disabled", true)
            addPreference("devtools.responsive.touchSimulation.enabled", true)
            addPreference("devtools.responsive.viewport.width", width)
            addPreference("devtools.responsive.viewport.height", height)
            addPreference("devtools.responsive.viewport.pixelRatio", pixelRatio)
            addPreference("devtools.responsive.userAgent", "$userAgent MicroMessenger")
            // XXX responsive 无法调用
            addPreference("general.useragent.override", "$userAgent MicroMessenger")
            addArguments("--width=${width}", "--height=${height}")
        }
        else -> throw IllegalArgumentException("未设置参数的浏览器")
    }
}

private val IS_READY_SCRIPT by lazy { JavaScriptLoader.load("IsReady") }

private val HAS_CARD_SCRIPT by lazy { JavaScriptLoader.load("HasCard") }

private val BROWSER_VERSION_SCRIPT by lazy { JavaScriptLoader.load("BrowserVersion") }

private val HIDE by lazy { JavaScriptLoader.load("Hide") }

val DEFAULT_HIDE_SELECTOR = arrayOf(".open-app", ".launch-app-btn", ".unlogin-popover", ".no-login")

private val Init = Duration.ofSeconds(10)

private val Timeout = Duration.ofMinutes(3)

private val Interval = Duration.ofSeconds(10)

internal const val HOME_PAGE = "https://t.bilibili.com/h5/dynamic/detail/508396365455813655"

object UserAgents {
    const val IPAD =
        "Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1"

    const val IPHONE =
        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"

    const val MAC =
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50"
}

fun RemoteWebDriver(config: RemoteWebDriverConfig): RemoteWebDriver {

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
        }
    }

    thread.contextClassLoader = oc

    return driver.getOrThrow()
}

private fun WebDriver.responsive() {
    val actions = Actions(this)
    actions.keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("m").keyUp(Keys.SHIFT).keyUp(Keys.CONTROL).perform()
}

suspend fun RemoteWebDriver.home(page: String = HOME_PAGE): Map<String, Boolean> {
    withTimeout(Timeout.toMillis()) {
        get(page)
    }
    @Suppress("UNCHECKED_CAST")
    return executeScript(BROWSER_VERSION_SCRIPT) as Map<String, Boolean>
}

internal fun RemoteWebDriver.isReady(): Boolean {
    return executeScript(IS_READY_SCRIPT) == true && executeScript(HAS_CARD_SCRIPT) == true
}

internal fun RemoteWebDriver.hide(vararg css: String): List<RemoteWebElement> {
    @Suppress("UNCHECKED_CAST")
    return executeScript(HIDE, *css) as ArrayList<RemoteWebElement>
}

suspend fun RemoteWebDriver.getScreenshot(url: String, vararg hide: String): ByteArray {
    val home = windowHandle
    val tab = switchTo().newWindow(WindowType.TAB) as RemoteWebDriver
    // tab.responsive()
    runCatching {
        withTimeout(Timeout.toMillis()) {
            tab.get(url)
            delay(Init.toMillis())
            while (isReady().not()) {
                delay(Interval.toMillis())
            }
        }
        hide(*hide)
    }.onFailure {
        it.printStackTrace()
    }
    val bytes = getScreenshotAs(OutputType.BYTES)
    tab.close()
    switchTo().window(home)
    return bytes
}