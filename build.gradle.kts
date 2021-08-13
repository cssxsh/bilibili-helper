plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin

    id("net.mamoe.mirai-console") version Versions.mirai
}

group = "xyz.cssxsh.mirai.plugin"
version = "1.0.7"

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
    jcenter()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
//            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
//            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
//            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
//            languageSettings.useExperimentalAnnotation("kotlinx.serialization.InternalSerializationApi")
//            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
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
        exclude("org.seleniumhq.selenium")
        exclude("junit")
        exclude("classworlds")
        exclude("io.netty")
        exclude("com.typesafe.netty")
    }
    implementation(selenium("java", Versions.selenium)) {
        exclude("io.netty")
        exclude("com.typesafe.netty")
        exclude("com.google.auto.service")
        exclude("com.google.guava")
        exclude("org.asynchttpclient")
        exclude("io.opentelemetry")
    }

    testImplementation(junit("api", Versions.junit))
    testRuntimeOnly(junit("engine", Versions.junit))
    testRuntimeOnly("org.slf4j", "slf4j-simple", "1.7.25")
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