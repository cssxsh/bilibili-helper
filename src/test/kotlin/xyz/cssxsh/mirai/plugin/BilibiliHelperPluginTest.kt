package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class BilibiliHelperPluginTest {

    @Test
    fun getScreenshot(): Unit = runBlocking {
        BilibiliHelperPlugin.getScreenshot(450055453856015371L)
    }
}