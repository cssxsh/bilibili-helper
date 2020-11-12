package xyz.cssxsh.mirai.plugin.tools

import org.openqa.selenium.OutputType
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.ProtocolHandshake
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.net.URL
import java.time.Duration
import java.util.logging.Level
import java.util.logging.Logger

class BilibiliChromeDriverTool(
    private val remoteAddress: URL,
    chromePath: String = "",
    deviceName: String = ""
) {

    companion object {
        init {
            ProtocolHandshake::class.java.getDeclaredField("LOG").apply {
                isAccessible = true
                (get(null) as Logger).level = Level.OFF
            }
        }

        val LOADED_SCRIPT = """
            function findVues() {
                let vues = [];
                try {
                    for(const element of document.getElementsByTagName("div")) {
                        if (element.__vue__) {
                            vues.push(element.__vue__);
                        }
                    }
                } finally {
                    //
                }
                return vues;
            };
            function vuesMounted() {
                let mounted = true;
                try {
                    for (const vue of findVues()) {
                        mounted = mounted && vue._isMounted;
                    }
                } finally {
                    //
                }
                return mounted;
            };
            function imgsComplete() {
                let complete = true
                try {
                    for(const element of document.getElementsByTagName("img")) {
                        complete = complete && element.complete;
                    }
                } finally {
                    //
                }
                return complete;
            };
            function isReady() {
                return document.readyState === 'complete' && vuesMounted() && imgsComplete();
            };
            return isReady();
        """.trimIndent()
    }

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
}