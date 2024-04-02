package xyz.cssxsh.bilibili

import kotlinx.coroutines.*
import xyz.cssxsh.bilibili.api.*
import kotlin.test.*

internal class VideoTest : ApiTest() {

    @Test
    @org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `user video`(): Unit = runBlocking {
        val result = client.getVideos(uid = 26798384L)
        assertFalse(result.list.videos.isEmpty())
    }

    @Test
    fun `video info by aid`(): Unit = runBlocking {
        val video = client.getVideoInfo(aid = 13502509L)

        assertEquals(13502509L, video.aid)
    }

    @Test
    fun `video info by bvid`(): Unit = runBlocking {
        val video = client.getVideoInfo(bvid = "BV1ex411J7GE")

        assertEquals("BV1ex411J7GE", video.id)
    }
}