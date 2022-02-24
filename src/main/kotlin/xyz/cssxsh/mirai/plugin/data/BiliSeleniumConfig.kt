package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.selenium.*

object BiliSeleniumConfig : ReadOnlyPluginConfig("SeleniumConfig"),
    RemoteWebDriverConfig by RemoteWebDriverConfig.INSTANCE {
    @ValueName("user_agent")
    @ValueDescription("截图UA")
    override val userAgent: String by value(UserAgents.IPAD + " MicroMessenger")

    @ValueName("width")
    @ValueDescription("截图宽度")
    override val width: Int by value(768)

    @ValueName("height")
    @ValueDescription("截图高度")
    override val height: Int by value(1024)

    @ValueName("headless")
    @ValueDescription("无头模式（后台模式）")
    override val headless: Boolean by value(true)

    private val DEFAULT_HIDE_SELECTOR = arrayOf(".open-app", ".launch-app-btn", ".unlogin-popover", ".no-login")

    @ValueName("hide")
    @ValueDescription("隐藏的web组件(jQ选择器)")
    val hide: Array<String> by value(DEFAULT_HIDE_SELECTOR)
}