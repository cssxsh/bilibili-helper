package xyz.cssxsh.mirai.bilibili.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.selenium.*

object BiliSeleniumConfig : ReadOnlyPluginConfig("SeleniumConfig") {
    @ValueName("user_agent")
    @ValueDescription("截图UA")
    val userAgent: String by value(UserAgents.IPAD + " MicroMessenger")

    @ValueName("width")
    @ValueDescription("截图宽度")
    val width: Int by value(768)

    @ValueName("height")
    @ValueDescription("截图高度")
    val height: Int by value(1024)

    @ValueName("headless")
    @ValueDescription("无头模式（后台模式）")
    val headless: Boolean by value(true)

    private val DEFAULT_HIDE_SELECTOR = arrayOf(".open-app", ".launch-app-btn", ".unlogin-popover", ".no-login")

    @ValueName("hide")
    @ValueDescription("隐藏的web组件(jQ选择器)")
    val hide: Array<String> by value(DEFAULT_HIDE_SELECTOR)

    object Agent : RemoteWebDriverConfig by RemoteWebDriverConfig.INSTANCE {
        override val userAgent: String get() = BiliSeleniumConfig.userAgent
        override val width: Int get() = BiliSeleniumConfig.width
        override val height: Int get() = BiliSeleniumConfig.height
        override val headless: Boolean get() = BiliSeleniumConfig.headless
    }
}