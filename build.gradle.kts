plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"

    id("net.mamoe.mirai-console") version "2.10.0-RC2"
    id("net.mamoe.maven-central-publish") version "0.7.0"
}

group = "xyz.cssxsh"
version = "1.4.1"

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/central")
    mavenCentral()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "bilibili-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

dependencies {
    implementation("io.ktor:ktor-client-serialization:1.6.5") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
        exclude(group = "io.ktor", module = "ktor-client-core")
        exclude(group = "io.ktor", module = "ktor-client-core-jvm")
    }
    implementation("io.ktor:ktor-client-encoding:1.6.5") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
        exclude(group = "io.ktor", module = "ktor-client-core")
        exclude(group = "io.ktor", module = "ktor-client-core-jvm")
    }
    compileOnly("xyz.cssxsh.mirai:mirai-selenium-plugin:2.0.6")
    compileOnly("net.mamoe:mirai-core:2.10.0-RC2")
    compileOnly("net.mamoe:mirai-core-utils:2.10.0-RC2")

    testImplementation(kotlin("test", "1.6.0"))
}

mirai {
    configureShadow {
        exclude("module-info.class")
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}