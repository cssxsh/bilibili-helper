package xyz.cssxsh.bilibili

import kotlinx.coroutines.*
import xyz.cssxsh.bilibili.api.*
import kotlin.test.*

internal class LiveApiTest : ApiTest() {

    @Test
    fun `room info`(): Unit = runBlocking {
        val room = client.getRoomInfo(roomId = 10112L)
        assertEquals(10112L, room.roomId)
        assertEquals(1590370L, room.uid)
    }

    @Test
    @org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `room info old`(): Unit = runBlocking {
        client.getRoomInfoOld(uid = 26798384L)
    }

    @Test
    fun `off live list`(): Unit = runBlocking {
        client.getOffLiveList(roomId = 10112L, count = 10)
    }

    @Test
    fun `round play video`(): Unit = runBlocking {
        client.getRoundPlayVideo(roomId = 10112L)
    }
}