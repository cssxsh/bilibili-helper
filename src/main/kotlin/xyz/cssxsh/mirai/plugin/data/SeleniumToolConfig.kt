package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.mirai.plugin.tools.RemoteWebDriverConfig

object SeleniumToolConfig : ReadOnlyPluginConfig("SeleniumConfig"), RemoteWebDriverConfig {
    private const val IPAD = "Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1"

    @ValueDescription("截图UA")
    override val userAgent: String by value(IPAD)
    @ValueDescription("截图宽度")
    override val width: Int by value(768)
    @ValueDescription("截图高度")
    override val height: Int by value(1024)
    @ValueDescription("截图像素比")
    override val pixelRatio: Int by value(3)
    @ValueDescription("是否截图模式")
    val setup: Boolean by value(true)
}