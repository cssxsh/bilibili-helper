package xyz.cssxsh.bilibili.api

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.bilibili.BilibiliClient

internal class BiliAPiTest {

    private val bilibiliClient = BilibiliClient(emptyMap())

    @Test
    fun accInfo(): Unit = runBlocking {
        bilibiliClient.accInfo(uid = 26798384L)
    }

    @Test
    fun dynamicInfo(): Unit = runBlocking {
        bilibiliClient.dynamicInfo(uid = 26798384L)
    }

    @Test
    fun roomInfo(): Unit = runBlocking {
        bilibiliClient.roomInfo(roomId = 10112L)
    }

    @Test
    fun searchVideo(): Unit = runBlocking {
        bilibiliClient.searchVideo(uid = 26798384L)
    }

    @Test
    fun videoInfo(): Unit = runBlocking {
        bilibiliClient.videoInfo(aid = 13502509L)
        bilibiliClient.videoInfo(bvId = "BV1ex411J7GE")
    }
}