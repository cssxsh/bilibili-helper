package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.mirai.plugin.tools.*

object SeleniumToolConfig : ReadOnlyPluginConfig("SeleniumConfig"), RemoteWebDriverConfig {
    private const val IPAD =
        "Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1"

    @ValueName("user_agent")
    @ValueDescription("截图UA")
    override val userAgent: String by value(IPAD)

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

    @ValueName("setup")
    @ValueDescription("是否截图模式")
    val setup: Boolean by value(true)
}