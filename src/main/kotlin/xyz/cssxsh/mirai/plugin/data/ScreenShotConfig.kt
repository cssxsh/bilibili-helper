package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

object ScreenShotConfig : AutoSavePluginConfig("ScreenShotConfig") {

    @ConsoleExperimentalApi
    override fun shouldPerformAutoSaveWheneverChanged(): Boolean = false

    @ValueName("driver_path")
    val driverPath: String by value("")

    @ValueName("chrome_path")
    val chromePath: String by value("")

    @ValueName("device_name")
    val deviceName: String by value("iPad")

    @ValueName("delay_millis")
    val delayMillis: Long by value(1_000L)
}