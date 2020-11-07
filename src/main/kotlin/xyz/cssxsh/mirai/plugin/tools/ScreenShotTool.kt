package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.delay
import org.openqa.selenium.OutputType
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

class ScreenShotTool(driverPath: String, chromePath: String? = null, deviceName: String? = null) {

    init {
        System.setProperty("webdriver.chrome.driver", driverPath)
    }

    private val driver = ChromeDriver(ChromeOptions().apply {
        addArguments("--headless")
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
    })

    suspend fun getScreenShot(url: String, delayMillis: Long = 10_000): ByteArray = driver.run {
        manage().window()
        get(url)
        delay(delayMillis)
        getScreenshotAs(OutputType.BYTES).also {
            quit()
        }
    }
}