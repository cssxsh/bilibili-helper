package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.utils.minutesToMillis
import net.mamoe.mirai.utils.secondsToMillis
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

        val IS_READY_SCRIPT = """
            function findVue() {
                let Vue = null;
                try {
                    for(const element of document.body.children) {
                        Vue = Vue || element.__vue__;
                    }
                } finally {
                    Vue = Vue || {};
                }
                return Vue;
            }
            function vmMounted(vm = findVue()) {
                let mounted = vm._isMounted;
                try {
                    for (const child of vm['$'+ 'children']) {
                        mounted = mounted && vmMounted(child);
                    }
                } finally {
                    //
                }
                return mounted;
            }
            function imagesComplete() {
                const images = document.getElementsByTagName("img");
                let complete = images.length !== 0;
                try {
                    for(const element of images) {
                        complete = complete && element.complete;
                    }
                } finally {
                    //
                }
                return complete;
            }
            return document.readyState === 'complete' && vmMounted() && imagesComplete();
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

    fun <R> useWait(
        driver: RemoteWebDriver,
        timeoutMillis: Long,
        intervalMillis: Long,
        block: (RemoteWebDriver) -> R
    ) = FluentWait(driver).withTimeout(Duration.ofMillis(timeoutMillis)).pollingEvery(Duration.ofMillis(intervalMillis))
        .until(block)

    fun <R> useDriver(block: (RemoteWebDriver) -> R) = RemoteWebDriver(remoteAddress, options).let { driver ->
        block(driver).also { driver.quit() }
    }

    fun getScreenShot(
        url: String,
        timeoutProgression: LongProgression = (1).secondsToMillis..(1).minutesToMillis step (1).secondsToMillis
    ): ByteArray = useDriver { driver ->
        driver.get(url)
        runBlocking { delay(timeoutProgression.first) }
        useWait(driver, timeoutProgression.last - timeoutProgression.first, timeoutProgression.step) {
            it.executeScript(IS_READY_SCRIPT)
        }
        driver.getScreenshotAs(OutputType.BYTES)
    }

    fun getScreenShot(
        url: String,
        timeoutMillis: Long,
    ) = getScreenShot(url, (1).secondsToMillis..timeoutMillis step (1).secondsToMillis)
}