package xyz.cssxsh.mirai.plugin

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class BilibiliUtilsKtTest {

    private val FILEPATH = "./test/config/bilibili-helper/BilibiliHelperConfig.json"



    private val cachePath by lazy {
        Json.parseToJsonElement(File(FILEPATH).readText()).jsonObject["cache_path"]!!.toString()
    }

    private fun getImage(type: CacheType, name: String): File =
        File(cachePath).resolve(type.name).resolve(name)

    private val DYNAMIC_REGEX = """dynamic-""".toRegex()

    private val LIVE_REGEX = """live-""".toRegex()

    @Test
    fun tranTest() {
        assertEquals(dynamicTimestamp(486812728869237544), 1612183321)
    }

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