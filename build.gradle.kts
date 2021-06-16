plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("net.mamoe.mirai-console")
}

group = "xyz.cssxsh.mirai.plugin"
version = "0.1.0-dev-3"

repositories {
    // mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/releases")
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
    jcenter()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.InternalSerializationApi")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.util.ConsoleExperimentalApi")
        }
        test {
            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.ConsoleFrontEndImplementation")
        }
    }
}

dependencies {
    implementation(ktor("client-serialization", Versions.ktor))
    implementation(ktor("client-encoding", Versions.ktor))
    implementation(mxlib("selenium", Versions.mxlib)) {
        exclude("org.seleniumhq.selenium","selenium-java")
        exclude("junit", "junit")
        exclude("classworlds", "classworlds")
    }
    implementation(selenium("java", Versions.selenium)) {
        exclude("io.netty")
    }
    implementation(project(":tools"))

    testImplementation(junit("api", Versions.junit))
    testRuntimeOnly(junit("engine", Versions.junit))
    testRuntimeOnly("org.slf4j", "slf4j-simple", "1.7.25")
}

mirai {
    configureShadow {
        exclude("module-info.class")
        exclude {
            it.path.startsWith("kotlin")
        }
        exclude {
            it.path.startsWith("io/ktor") &&
                (it.path.startsWith("io/ktor/client/features/compression") || it.path.startsWith("io/ktor/client/features/json")).not()
        }
        exclude {
            it.path.startsWith("io/netty")
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}