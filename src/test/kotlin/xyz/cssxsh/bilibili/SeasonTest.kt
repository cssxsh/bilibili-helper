package xyz.cssxsh.bilibili

import kotlinx.coroutines.*
import xyz.cssxsh.bilibili.api.*
import kotlin.test.*

internal class SeasonTest : ApiTest() {

    @Test
    fun `season media`(): Unit = runBlocking {
        val media = client.getSeasonMedia(mediaId = 28233903).media
        assertEquals(28233903, media.mediaId)
    }

    @Test
    fun `season info`(): Unit = runBlocking {
        val season = client.getSeasonInfo(seasonId = 38234)
        assertEquals(38234, season.seasonId)
    }

    @Test
    fun `season section`(): Unit = runBlocking {
        val section = client.getSeasonSection(seasonId = 38221)
        assertEquals(57667, section.mainSection.id)
    }

    @Test
    fun `season episode`(): Unit = runBlocking {
        val episode = client.getEpisodeInfo(episodeId = 395240)
        assertEquals(395240, episode.new?.id)
    }

    @Test
    fun `season timeline`(): Unit = runBlocking {
        val timeline = client.getSeasonTimeline()
        assertFalse(timeline.isEmpty())
    }
}