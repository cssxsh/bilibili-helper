package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

object ScreenShotToolConfig : AutoSavePluginConfig("HelperConfig") {

    @ConsoleExperimentalApi
    override fun shouldPerformAutoSaveWheneverChanged(): Boolean = false

    @ValueName("driver_path")
    val driverPath: String? by value(null)

    @ValueName("driver_path")
    val chromePath: String? by value(null)

    @ValueName("device_name")
    val deviceName: String? by value("iPad")

    @ValueName("delay_millis")
    val delayMillis: Long by value(1_000L)
}