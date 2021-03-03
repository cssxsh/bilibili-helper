package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.mirai.plugin.tools.SeleniumTool
import kotlin.time.*

object SeleniumToolConfig : ReadOnlyPluginConfig("SeleniumToolConfig") {
    @ValueName("driver_url")
    val driverUrl: String by value("http://127.0.0.1:9515")

    @ValueName("driver_type")
    val driverType: SeleniumTool.DriverType by value(SeleniumTool.DriverType.CHROME)

    @ValueName("device_name")
    val deviceName: String by value("iPad")

    @ValueName("timeout_millis")
    val timeoutMillis: Long by value((1).minutes.toLongMilliseconds())
}