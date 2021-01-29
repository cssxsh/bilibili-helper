package xyz.cssxsh.mirai.plugin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class BilibiliUtilsKtTest {

    private val cachePath = "./ImageCache"

    private fun getImage(type: CacheType, name: String): File =
        File(cachePath).resolve(type.name).resolve(name)

    private val DYNAMIC_REGEX = """dynamic-""".toRegex()

    private val LIVE_REGEX = """live-""".toRegex()

    @Test
    fun moveFiles() {
        File(cachePath).listFiles { file ->
            file.isFile
        }?.forEach { file ->
            when {
                DYNAMIC_REGEX in file.name -> {
                    val timestamp = dynamicTimestamp("""\d+""".toRegex().find(file.name)!!.value.toLong())
                    println(timestamp)
                    val filename = "${timestampToLocalDate(timestamp)}/${file.name.replace(DYNAMIC_REGEX, "")}"
                    file.renameTo(getImage(CacheType.DYNAMIC, filename).apply {
                        parentFile.mkdirs()
                    })
                }
                LIVE_REGEX in file.name -> {
                    val filename = file.name.replace(LIVE_REGEX, "")
                    file.renameTo(getImage(CacheType.LIVE, filename).apply {
                        parentFile.mkdirs()
                    })
                }
            }
        }
    }
}