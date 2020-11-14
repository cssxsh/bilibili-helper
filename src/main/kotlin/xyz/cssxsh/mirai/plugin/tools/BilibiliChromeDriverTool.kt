package xyz.cssxsh.mirai.plugin.tools

import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.ProtocolHandshake
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
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

    suspend fun <R> useDriver(block: suspend (RemoteWebDriver) -> R) = RemoteWebDriver(remoteAddress, options).let { driver ->
        block(driver).also { driver.quit() }
    }
}