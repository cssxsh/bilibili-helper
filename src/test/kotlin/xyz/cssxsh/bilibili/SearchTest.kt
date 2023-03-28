package xyz.cssxsh.bilibili

import kotlinx.coroutines.*
import org.junit.jupiter.api.condition.*
import xyz.cssxsh.bilibili.api.*
import kotlin.test.*

internal class SearchTest : ApiTest() {

    @Test
    fun `search user`(): Unit = runBlocking {
        val user = client.searchUser(keyword = "瓶子君152")
            .result.find { user -> user.mid == 730732L }
        assertNotNull(user)
        assertEquals(42062, user.roomId)
        assertEquals("瓶子君152", user.name)
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `search bangumi`(): Unit = runBlocking {
        val season = client.searchBangumi(keyword = "SSSS")
            .result.find { season -> season.seasonId == 38233L }
        assertNotNull(season)
        assertEquals("""<em class="keyword">SSSS</em>.电光机王""", season.title)
        assertEquals(28233915, season.mediaId)
        assertFalse(assertNotNull(season.episodes).isEmpty())
        assertNotNull(season.rating)
    }

    @Test
    fun `search ft`(): Unit = runBlocking {
        val season = client.searchFT(keyword = "让子弹飞")
            .result.find { season -> season.seasonId == 12548L }
        assertNotNull(season)
        assertEquals("""<em class="keyword">让子弹飞</em>""", season.title)
        assertEquals(80952, season.mediaId)
        assertNotNull(season.rating)
    }

    @Test
    fun `search live room`(): Unit = runBlocking {
        val room = client.searchLiveRoom(keyword = "音悦台")
            .result.find { room -> room.roomId == 23058L }
        assertNotNull(room)
        assertEquals(3, room.shortId)
        assertEquals(11153765, room.uid)
        assertNotNull(room) { room.liveTime }
    }
}