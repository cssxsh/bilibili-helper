@file:Suppress("UnstableApiUsage")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.4.30"
        kotlin("plugin.serialization") version "1.4.30"

        id("net.mamoe.mirai-console") version "2.6.4"
    }
    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/releases")
        maven(url = "https://maven.aliyun.com/repository/public")
        mavenCentral()
        jcenter()
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
        gradlePluginPortal()
    }
}
rootProject.name = "bilibili-helper"

include("tools")