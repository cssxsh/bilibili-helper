@file:Suppress("unused")

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.kotlinx(module: String, version: String) =
    "org.jetbrains.kotlinx:kotlinx-$module:$version"

fun DependencyHandler.ktor(module: String, version: String) =
    "io.ktor:ktor-$module:$version"

fun DependencyHandler.mirai(module: String, version: String) =
    "net.mamoe:mirai-$module:$version"

fun DependencyHandler.selenium(module: String, version: String) =
    "org.seleniumhq.selenium:selenium-$module:$version"

fun DependencyHandler.junit(module: String, version: String) =
    "org.junit.jupiter:junit-jupiter-${module}:${version}"