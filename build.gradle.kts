
plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("net.mamoe.mirai-console") version Versions.mirai
}

group = "xyz.cssxsh.mirai.plugin"
version = "0.1.0-dev-1"

repositories {
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/releases")
    maven(url = "https://mirrors.huaweicloud.com/repository/maven")
    // bintray dl.bintray.com -> bintray.proxy.ustclug.org
    maven(url = "https://bintray.proxy.ustclug.org/him188moe/mirai/")
    maven(url = "https://bintray.proxy.ustclug.org/kotlin/kotlin-dev")
    maven(url = "https://bintray.proxy.ustclug.org/kotlin/kotlinx/")
    // central
    maven(url = "https://maven.aliyun.com/repository/central")
    mavenCentral()
    // jcenter
    maven(url = "https://maven.aliyun.com/repository/jcenter")
    jcenter()
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.InternalSerializationApi")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
        }
        test {
            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.ConsoleFrontEndImplementation")
        }
    }
}

dependencies {
    implementation(ktor("client-serialization", Versions.ktor))
    implementation(ktor("client-encoding", Versions.ktor))
    implementation(selenium("java", Versions.selenium))
    testImplementation(junit("api", Versions.junit))
    testRuntimeOnly(junit("engine", Versions.junit))
}

tasks {

    test {
        useJUnitPlatform()
    }

    val testConsoleDir = rootProject.projectDir.resolve( "test").apply { mkdir() }

    create("copyFile") {
        group = "mirai"

        dependsOn("buildPlugin")

        doFirst {
            testConsoleDir.resolve( "plugins/").walk().filter {
                project.name in it.name
            }.forEach {
                delete(it)
                println("Deleted ${it.absolutePath}")
            }
            copy {
                into(testConsoleDir.resolve("plugins/"))
                from(project.buildDir.resolve("mirai/")) {
                    include {
                        "${project.name}-${version}" in it.name
                    }.eachFile {
                        println("Copy ${file.absolutePath}")
                    }
                }
            }
            testConsoleDir.resolve("start.sh").writeText(
                buildString {
                    appendln("cd ${testConsoleDir.absolutePath}")
                    appendln("java -classpath ${sourceSets.test.get().runtimeClasspath.asPath} \\")
                    appendln("-Dfile.encoding=UTF-8 \\")
                    appendln("mirai.RunMirai")
                }
            )
        }
    }

    create("runMiraiConsole", JavaExec::class.java) {
        group = "mirai"

        dependsOn("copyFile")

        main = "mirai.RunMirai"

        // debug = true

        defaultCharacterEncoding = "UTF-8"

        workingDir = testConsoleDir

        standardInput = System.`in`

        // jvmArgs("-Djavax.net.debug=all")

        doFirst {
            classpath = sourceSets.test.get().runtimeClasspath
            println("WorkingDir: ${workingDir.absolutePath}, Args: $args")
        }
    }
}