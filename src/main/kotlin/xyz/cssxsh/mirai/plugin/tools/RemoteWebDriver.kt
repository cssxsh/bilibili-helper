package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.delay
import org.openqa.selenium.OutputType
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.time.Duration
import kotlin.time.*

internal val IS_READY_SCRIPT by lazy {
    SeleniumTool.loadJavaScript("IsReady")
}

internal val HAS_CONTENT by lazy {
    SeleniumTool.loadJavaScript("HasContent")
}

fun <R> RemoteWebDriver.useWait(
    timeoutMillis: Long,
    intervalMillis: Long,
    block: (RemoteWebDriver) -> R
): R = FluentWait(this).withTimeout(Duration.ofMillis(timeoutMillis)).pollingEvery(Duration.ofMillis(intervalMillis))
    .until(block)

suspend fun RemoteWebDriver.getScreenShot(
    url: String,
    timeoutProgression: LongProgression = (1).seconds.toLongMilliseconds()..(1).minutes.toLongMilliseconds() step (1).seconds.toLongMilliseconds()
): ByteArray {
    get(url)
    delay(timeoutProgression.first)
    useWait(timeoutProgression.last - timeoutProgression.first, timeoutProgression.step) { driver ->
        (driver.executeScript(IS_READY_SCRIPT) == true) && (driver.executeScript(HAS_CONTENT) == true)
    }
    return getScreenshotAs(OutputType.BYTES)
}

suspend fun RemoteWebDriver.getScreenShot(
    url: String,
    timeoutMillis: Long,
) = getScreenShot(url, (1).seconds.toLongMilliseconds()..timeoutMillis step (1).seconds.toLongMilliseconds())