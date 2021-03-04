package xyz.cssxsh.bilibili.api

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.bilibili.BilibiliClient

internal class BilibiliApiTest {

    private val client = BilibiliClient(emptyList())

    private fun <T> T.withPrintln(): T = also { println(it) }

    @Test
    fun getArticleTest(): Unit = runBlocking {
        client.getArticleInfo(cid = 10018100).withPrintln()
    }

    @Test
    fun getDynamicTest(): Unit = runBlocking {
        client.getSpaceHistory(uid = 26798384L).withPrintln()

        client.getDynamicInfo(dynamicId = 450055453856015371L).withPrintln()
    }

    @Test
    fun getLiveTest(): Unit = runBlocking {
        client.getRoomInfo(roomId = 10112L).withPrintln()

        client.getRoomInfoOld(mid = 26798384L).withPrintln()

        client.getOffLiveList(roomId = 10112L, count = 10).withPrintln()

        client.getRoundPlayVideo(roomId = 10112L).withPrintln()
    }

    @Test
    fun getUserTest(): Unit = runBlocking {
        client.getUserInfo(mid = 26798384L).withPrintln()
    }

    @Test
    fun getVideoInfoTest(): Unit = runBlocking {
        client.searchVideo(mid = 26798384L).withPrintln()

        client.getVideoInfo(aid = 13502509L).withPrintln()

        client.getVideoInfo(bvid = "BV1ex411J7GE").withPrintln()
    }
}