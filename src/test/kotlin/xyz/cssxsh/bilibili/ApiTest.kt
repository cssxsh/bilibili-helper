package xyz.cssxsh.bilibili

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import xyz.cssxsh.bilibili.api.*

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

        client.getRoomInfoOld(mid = 26798384L)

        client.getOffLiveList(roomId = 10112L, count = 10)

        client.getRoundPlayVideo(roomId = 10112L)
    }

    @Test
    fun season(): Unit = runBlocking {
        withLog(client.getSeasonMedia(28233903).media) { info ->
            appendLine(info.content)
        }
        withLog(client.getSeasonInfo(38234)) { info ->
            appendLine(info.content)
        }
        withLog(client.getEpisodeInfo(395240)) { info ->
            appendLine(info.content)
        }
        withLog(client.getSeasonSection(38221)) { info ->
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
        client.getUserInfo(mid = 26798384L)
    }

    @Test
    fun video(): Unit = runBlocking {
        withLog(client.getVideos(mid = 26798384L).list.videos) { list ->
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
        withLog(client.searchUser(keyword = "sss").result) {
            it.forEach { user ->
                appendLine(user.content)
            }
        }
        withLog(client.searchBangumi(keyword = "SSSS").result) {
            it.forEach { season ->
                appendLine(season.content)
            }
        }
        withLog(client.searchFT(keyword = "非自然死亡").result) {
            it.forEach { season ->
                appendLine(season.content)
            }
        }
    }
}