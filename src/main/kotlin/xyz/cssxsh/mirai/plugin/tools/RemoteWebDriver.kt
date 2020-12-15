package xyz.cssxsh.mirai.plugin.tools

import kotlinx.coroutines.delay
import net.mamoe.mirai.utils.minutesToMillis
import net.mamoe.mirai.utils.secondsToMillis
import org.openqa.selenium.OutputType
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
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
        (it.executeScript(BilibiliChromeDriverTool.IS_READY_SCRIPT) as Boolean) &&
            (it.executeScript(BilibiliChromeDriverTool.HAS_CONTENT) as Boolean)
    }
    return getScreenshotAs(OutputType.BYTES).also { quit() }
}

suspend fun RemoteWebDriver.getScreenShot(
    url: String,
    timeoutMillis: Long,
) = getScreenShot(url, (1).secondsToMillis..timeoutMillis step (1).secondsToMillis)