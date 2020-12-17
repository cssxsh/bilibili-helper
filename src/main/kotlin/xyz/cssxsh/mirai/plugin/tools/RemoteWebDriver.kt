package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.delay
import net.mamoe.mirai.utils.minutesToMillis
import net.mamoe.mirai.utils.secondsToMillis
import org.openqa.selenium.OutputType
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import xyz.cssxsh.mirai.plugin.tools.BilibiliChromeDriverTool.Companion.IS_READY_SCRIPT
import xyz.cssxsh.mirai.plugin.tools.BilibiliChromeDriverTool.Companion.HAS_CONTENT
import java.time.Duration

fun <R> RemoteWebDriver.useWait(
    timeoutMillis: Long,
    intervalMillis: Long,
    block: (RemoteWebDriver) -> R
): R = FluentWait(this).withTimeout(Duration.ofMillis(timeoutMillis)).pollingEvery(Duration.ofMillis(intervalMillis))
    .until(block)

suspend fun RemoteWebDriver.getScreenShot(
    url: String,
    timeoutProgression: LongProgression = (1).secondsToMillis..(1).minutesToMillis step (1).secondsToMillis
): ByteArray {
    get(url)
    delay(timeoutProgression.first)
    useWait(timeoutProgression.last - timeoutProgression.first, timeoutProgression.step) {
        (it.executeScript(IS_READY_SCRIPT) == true) && it.findElementsByClassName("content").isNullOrEmpty()
    }
    return getScreenshotAs(OutputType.BYTES)
}

suspend fun RemoteWebDriver.getScreenShot(
    url: String,
    timeoutMillis: Long,
) = getScreenShot(url, (1).secondsToMillis..timeoutMillis step (1).secondsToMillis)