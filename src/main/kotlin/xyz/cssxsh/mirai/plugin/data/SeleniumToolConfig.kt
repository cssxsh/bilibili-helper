package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object SeleniumToolConfig : ReadOnlyPluginConfig("SeleniumConfig") {
    @ValueName("device_name")
    val device: String by value("iPad")
}