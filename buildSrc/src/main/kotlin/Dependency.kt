@file:Suppress("unused")

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.ktor(module: String, version: String) = "io.ktor:ktor-$module:$version"

fun DependencyHandler.mirai(module: String, version: String) = "net.mamoe:mirai-$module:$version"