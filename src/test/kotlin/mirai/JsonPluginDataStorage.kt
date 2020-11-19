package mirai

import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.data.MultiFilePluginDataStorage
import net.mamoe.mirai.console.data.PluginData
import net.mamoe.mirai.console.data.PluginDataHolder
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.*
import java.io.File
import java.nio.file.Path

@ConsoleExperimentalApi
class JsonPluginDataStorage(
    override val directoryPath: Path,
    isConfig: Boolean,
) : MultiFilePluginDataStorage {
    init {
        directoryPath.toFile().mkdir()
    }

    companion object {
        private val logger: MiraiLogger by lazy {
            MiraiConsole.createLogger("DataStorage")
        }
    }

    private val json = Json {
        prettyPrint = isConfig
        ignoreUnknownKeys = true
        isLenient = isConfig
        allowStructuredMapKeys = true
    }

    override fun load(holder: PluginDataHolder, instance: PluginData) {
        instance.onInit(holder, this)
        getPluginDataFile(holder, instance).readText().let { text ->
            if (text.isNotBlank()) {
                json.decodeFromString(instance.updaterSerializer, text)
            } else {
                store(holder, instance) // save an initial copy
            }
        }
        logger.verbose { "Successfully loaded PluginData: ${instance.saveName}" }
    }

    private fun getPluginDataFile(holder: PluginDataHolder, instance: PluginData): File = directoryPath.run {
        resolve(holder.dataHolderName).toFile()
    }.apply {
        require(isFile.not()) {
            "Target directory $path for holder $holder is occupied by a file therefore data ${instance::class.qualifiedName} can't be saved."
        }
        mkdir()
    }.resolve("${instance.saveName}.json").apply {
        require(isDirectory.not()) {
            "Target File $absolutePath is occupied by a directory therefore data ${instance::class.qualifiedName} can't be saved."
        }
        logger.verbose { "File allocated for ${instance.saveName}: $absolutePath" }
        createNewFile()
    }

    override fun store(holder: PluginDataHolder, instance: PluginData) {
        runCatching {
            getPluginDataFile(holder, instance).writeText(
                json.encodeToString(instance.updaterSerializer, {}())
            )
        }.onSuccess {
            logger.verbose { "Successfully saved PluginData: ${instance.saveName}" }
        }.onFailure {
            throw IllegalStateException("Exception while saving $instance, saveName=${instance.saveName}", it)
        }
    }
}