plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin

    id("net.mamoe.mirai-console") version Versions.mirai
    id("net.mamoe.maven-central-publish") version "0.7.0"
}

group = "xyz.cssxsh"
version = "1.2.8"

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

kotlin {
    sourceSets {
        all {
           // languageSettings.optIn("net.mamoe.mirai.console.util.ConsoleExperimentalApi")
        }
        test {
            languageSettings.optIn("net.mamoe.mirai.console.ConsoleFrontEndImplementation")
        }
    }
}

dependencies {
    implementation(ktor("client-serialization", Versions.ktor))
    implementation(ktor("client-encoding", Versions.ktor))
    compileOnly("xyz.cssxsh.mirai:mirai-selenium-plugin:1.0.3")
    compileOnly("net.mamoe:mirai-core-jvm:${Versions.mirai}")

    testImplementation(kotlin("test", Versions.kotlin))
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
    configureShadow {
        exclude("module-info.class")
        exclude {
            it.path.startsWith("kotlin")
        }
        exclude {
            it.path.startsWith("io/ktor") &&
                (it.path.startsWith("io/ktor/client/features/compression") || it.path.startsWith("io/ktor/client/features/json")).not()
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}