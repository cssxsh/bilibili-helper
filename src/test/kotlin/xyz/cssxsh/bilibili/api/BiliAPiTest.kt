package xyz.cssxsh.bilibili.api

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.bilibili.BilibiliClient

internal class BiliAPiTest {

    private val bilibiliClient = BilibiliClient(emptyList())

    @Test
    fun getAccInfoTest(): Unit = runBlocking {
        bilibiliClient.getAccInfo(uid = 26798384L)
    }

    @Test
    fun getSpaceHistoryTest(): Unit = runBlocking {
        bilibiliClient.getSpaceHistory(uid = 26798384L)
    }
    
    @Test
    fun getDynamicDetailTest(): Unit = runBlocking {
        bilibiliClient.getDynamicDetail(dynamicId = 450055453856015371L)
    }

    @Test
    fun getRoomInfoTest(): Unit = runBlocking {
        bilibiliClient.getRoomInfo(roomId = 10112L)
    }

    @Test
    fun searchVideoTest(): Unit = runBlocking {
        bilibiliClient.searchVideo(uid = 26798384L)
    }

    @Test
    fun getVideoInfoTest(): Unit = runBlocking {
        bilibiliClient.getVideoInfo(aid = 13502509L)
        bilibiliClient.getVideoInfo(bvid = "BV1ex411J7GE")
    }
}