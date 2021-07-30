package xyz.cssxsh.bilibili

import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.*
import java.io.File

internal class ApiTest {

    private val client = BiliClient()

    private inline fun <T> withLog(receiver: T, block: StringBuilder.(info: T) -> Unit) {
        logger.info("\n" + buildString { block(receiver) })
    }

    private val logger by lazy { LoggerFactory.getLogger(this::class.java) }

    @Test
    fun article(): Unit = runBlocking {
        withLog(client.getArticleInfo(cid = 10018100).last) {
            appendLine(it.content)
            appendLine(it.images)
        }
    }

    @Test
    fun dynamic(): Unit = runBlocking {
        withLog(client.getDynamicInfo(dynamicId = 450055453856015371L).dynamic) {
            appendLine(it.content)
            appendLine(it.images)
        }
        withLog(client.getSpaceHistory(uid = 26798384L).dynamics) { list ->
            list.forEach {
                appendLine(it.content)
                appendLine(it.images)
                appendLine("=================")
            }
        }
    }

    @Test
    fun live(): Unit = runBlocking {
        client.getRoomInfo(roomId = 10112L)

        client.getRoomInfoOld(uid = 26798384L)

        client.getOffLiveList(roomId = 10112L, count = 10)

        client.getRoundPlayVideo(roomId = 10112L)
    }

    @Test
    fun season(): Unit = runBlocking {
        withLog(client.getSeasonMedia(mediaId = 28233903).media) { info ->
            appendLine(info.content)
        }
        withLog(client.getSeasonInfo(seasonId = 38234)) { info ->
            appendLine(info.content)
        }
        withLog(client.getEpisodeInfo(episode_id = 395240)) { info ->
            appendLine(info.content)
        }
        withLog(client.getSeasonSection(seasonId = 38221)) { info ->
            appendLine(info.mainSection.episodes.last())
        }
        withLog(client.getSeasonTimeline()) { list ->
            list.forEach {
                appendLine(it)
            }
        }
    }

    @Test
    fun user(): Unit = runBlocking {
        client.getUserInfo(uid = 26798384L)
    }

    @Test
    fun video(): Unit = runBlocking {
        withLog(client.getVideos(uid = 26798384L).list.videos) { list ->
            list.forEach {
                appendLine(it.content)
                appendLine(it.cover)
                appendLine("=================")
            }
        }

        withLog(client.getVideoInfo(aid = 13502509L)) {
            appendLine(it.content)
            appendLine(it.cover)
        }

        withLog(client.getVideoInfo(bvid = "BV1ex411J7GE")) {
            appendLine(it.content)
            appendLine(it.cover)
        }
    }

    @Test
    fun search(): Unit = runBlocking {
        withLog(client.searchUser(keyword = "瓶子君152").result) {
            it.forEach { user ->
                appendLine(user.content)
            }
        }
        withLog(client.searchBangumi(keyword = "SSSS").result) {
            it.forEach { season ->
                appendLine(season.content)
            }
        }
        withLog(client.searchFT(keyword = "让子弹飞").result) {
            it.forEach { season ->
                appendLine(season.content)
            }
        }
    }

    @Test
    fun suit(): Unit = runBlocking {
        val dir = File("F:\\BilibiliCache\\EMOJI\\")
        client.getGarbSuit(itemId = 2452).emoji.flatMap { it.items.orEmpty() }.forEach { item ->
            val image = (item.properties["image"] as JsonPrimitive).content
            dir.resolve("${item.name}.${image.substringAfterLast('.')}").apply {
                if (exists().not()) {
                    writeBytes(client.useHttpClient { it.get(image) })
                }
            }
        }
    }
}