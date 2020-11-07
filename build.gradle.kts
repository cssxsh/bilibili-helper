
plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    kotlin("kapt") version Versions.kotlin
    id("com.github.johnrengelman.shadow") version Versions.shadow
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
    maven(url = "https://bintray.proxy.ustclug.org/korlibs/korlibs/")
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
            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
        }
        test {
            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.ConsoleFrontEndImplementation")
        }
    }
}

dependencies {
    kapt(group = "com.google.auto.service", name = "auto-service", version = Versions.autoService)
    compileOnly(group = "com.google.auto.service", name = "auto-service-annotations", version = Versions.autoService)
    implementation(mirai("core", Versions.core))
    implementation(mirai("console", Versions.console))
    implementation(ktor("client-core", Versions.ktor))
    implementation(ktor("client-serialization", Versions.ktor))
    implementation(ktor("client-encoding", Versions.ktor))
    implementation(ktor("client-okhttp", Versions.ktor))
    // test
    testImplementation(mirai("core-qqandroid", Versions.core))
    testImplementation(mirai("console-terminal", Versions.console))
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = Versions.junit)
}

tasks {

    test {
        useJUnitPlatform()
    }

    shadowJar {
        dependencies {
            exclude { "org.jetbrains" in it.moduleGroup }
            exclude { "net.mamoe" in it.moduleGroup }
        }
    }

    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
        kotlinOptions.jvmTarget = "11"
    }

    compileTestKotlin {
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
        kotlinOptions.jvmTarget = "11"
    }

    val testConsoleDir = File(parent?.projectDir ?: projectDir, "test").apply { mkdir() }

    create("copyFile") {
        group = "mirai"

        dependsOn(shadowJar)
        dependsOn(testClasses)


        doFirst {
            File(testConsoleDir, "classes/").walk().forEach {
                delete(it)
                println("Deleted ${it.absolutePath}")
            }
            File(testConsoleDir, "plugins/").walk().filter {
                project.name in it.name
            }.forEach {
                delete(it)
                println("Deleted ${it.absolutePath}")
            }
            copy {
                into(File(testConsoleDir, "classes/"))
                from(sourceSets["test"].runtimeClasspath.files) {
                    exclude {
                        "class" in it.path || it.isDirectory
                    }
                    eachFile {
                        println("Copy ${file.absolutePath}")
                    }
                }
                from(sourceSets["test"].runtimeClasspath.files) {
                    include {
                        "run" in it.path || "mirai" in it.path
                    }
                    eachFile {
                        println("Copy ${file.absolutePath}")
                    }
                }
            }
            copy {
                into(File(testConsoleDir, "plugins/"))
                from(File(project.buildDir, "libs/")) {
                    include {
                        "${project.name}-${version}-all" in it.name
                    }.eachFile {
                        println("Copy ${file.absolutePath}")
                    }
                }
            }
            File(testConsoleDir, "start.bat").writeText(
                buildString {
                    appendln("cd ${testConsoleDir.absolutePath}")
                    appendln("@echo off")
                    appendln("java -classpath ${File(testConsoleDir, "classes/").walk().joinToString(";")} ^")
                    appendln("-Dfile.encoding=UTF-8 ^")
                    appendln("mirai.RunMirai")
                }
            )
            File(testConsoleDir, "start.sh").writeText(
                buildString {
                    appendln("cd ${testConsoleDir.absolutePath}")
                    appendln("java -classpath ${sourceSets["test"].runtimeClasspath.asPath} \\")
                    appendln("-Dfile.encoding=UTF-8 \\")
                    appendln("mirai.RunMirai")
                }
            )
        }
    }

    create("runMiraiConsole", JavaExec::class.java) {
        group = "mirai"

        dependsOn(named("copyFile"))

        main = "mirai.RunMirai"

        // debug = true

        defaultCharacterEncoding = "UTF-8"

        workingDir = testConsoleDir

        standardInput = System.`in`

        // jvmArgs("-Djavax.net.debug=all")

        doFirst {
            classpath = sourceSets["test"].runtimeClasspath
            println("WorkingDir: ${workingDir.absolutePath}, Args: $args")
        }
    }
}