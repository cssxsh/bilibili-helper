package xyz.cssxsh.mirai.plugin.tools

import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.ProtocolHandshake
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.util.concurrent.TimeUnit
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

        val IS_READY_SCRIPT by lazy {
            this::class.java.getResourceAsStream("isReady.js")!!.use {
                it.reader().readText() + "\nreturn isReady();"
            }
        }

        val HAS_CONTENT by lazy {
            this::class.java.getResourceAsStream("hasContent.js")!!.use {
                it.reader().readText() + "\nreturn hasContent();"
            }
        }
    }

    private val options = ChromeOptions().apply {
        setHeadless(true)
        setPageLoadStrategy(PageLoadStrategy.NORMAL)
        setExperimentalOption("excludeSwitches", listOf(
            "enable-automation",
            "ignore-certificate-errors"
        ))
        if (chromePath.isNotBlank()) {
            setBinary(chromePath)
        }
        if (deviceName.isNotBlank()) {
            setExperimentalOption("mobileEmulation", mapOf("deviceName" to deviceName))
        }
    }

    suspend fun <R> useDriver(block: suspend (RemoteWebDriver) -> R) =
        RemoteWebDriver(remoteAddress, options).let { driver ->
            driver.manage().timeouts().pageLoadTimeout(10L, TimeUnit.MINUTES)
            driver.manage().timeouts().setScriptTimeout(10L, TimeUnit.SECONDS)
            kotlin.runCatching {
                block(driver)
            }.also {
                driver.quit()
            }.getOrThrow()
        }
}