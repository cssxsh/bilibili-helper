package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.mirai.plugin.tools.*

object SeleniumToolConfig : ReadOnlyPluginConfig("SeleniumConfig"), RemoteWebDriverConfig {
    @ValueName("user_agent")
    @ValueDescription("截图UA")
    override val userAgent: String by value(UserAgents.IPAD)

    @ValueName("width")
    @ValueDescription("截图宽度")
    override val width: Int by value(768)

    @ValueName("height")
    @ValueDescription("截图高度")
    override val height: Int by value(1024)

    @ValueName("pixel_ratio")
    @ValueDescription("截图像素比")
    override val pixelRatio: Int by value(3)

    @ValueName("headless")
    @ValueDescription("无头模式（后台模式）")
    override val headless: Boolean by value(true)

    @ValueName("browser")
    @ValueDescription("指定使用的浏览器，Chrome/firefox")
    override val browser: String by value("")

    @ValueName("home")
    @ValueDescription("浏览器会保持打开主页，以加快其他页面加载速度")
    val home: String by value(DEFAULT_HOME_PAGE)

    @ValueName("hide")
    @ValueDescription("隐藏的web组件(jQ选择器)")
    val hide: Array<String> by value(DEFAULT_HIDE_SELECTOR)

    @ValueName("setup")
    @ValueDescription("是否截图模式")
    val setup: Boolean by value(true)
}