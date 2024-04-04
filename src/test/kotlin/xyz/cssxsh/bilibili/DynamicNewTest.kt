package xyz.cssxsh.bilibili

import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import xyz.cssxsh.bilibili.api.*
import xyz.cssxsh.bilibili.data.dynamic.*
import kotlin.test.*

internal class DynamicNewTest : ApiTest() {

    private suspend fun fetch(id: Long, flush: Boolean = false): DynamicDetail {
        val folder = java.io.File("run", "fetch")
        folder.mkdirs()
        val json = folder.resolve("${id}.json")

        if (json.exists() && flush.not()) {
            return Json.decodeFromString(DynamicDetail.serializer(), json.readText())
        }

        val data = client.getDynamicDetail(dynamicId = id).item
        json.writeText(Json.encodeToString(DynamicDetail.serializer(), data))

        return data
    }

    @Test
    fun `type DYNAMIC_TYPE_NONE`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/716510857084796964
            716510857084796964
        ).forEach { id ->
            val forward = fetch(id = id) as DynamicDetail.Forward
            val none = forward.original
            assertIs<DynamicMajor.None>(none.major)
            assertIs<DynamicDetail.None>(none)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_FORWARD`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/649724369116856353
            649724369116856353,
            // https://t.bilibili.com/866756840240709701
            866756840240709701
        ).forEach { id ->
            val forward = fetch(id = id)
            assertEquals(id, forward.id)
            assertIs<DynamicDetail.Forward>(forward)
            assertNotNull(forward.modules.dynamic.describe)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_AV`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/666029864355102737
            666029864355102737,
            // https://t.bilibili.com/716526237365829703
            716526237365829703,
            // https://t.bilibili.com/716751108968546393
            716751108968546393,
            // https://t.bilibili.com/721295772787671059
            721295772787671059
        ).forEach { id ->
            val video = fetch(id = id)
            assertEquals(id, video.id)
            assertIs<DynamicMajor.Archive>(video.major)
            assertIs<DynamicDetail.Video>(video)
            assertNotEquals("", video.major.content.bvid)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_WORD`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/716365292050055176
            716365292050055176,
            // https://t.bilibili.com/718377531474968613
            718377531474968613,
            // https://t.bilibili.com/721203899129659408
            721203899129659408,
            // https://t.bilibili.com/759470747832811592
            759470747832811592,
            // https://t.bilibili.com/870176712256651305
            870176712256651305
        ).forEach { id ->
            val word = fetch(id = id)
            assertEquals(id, word.id)
            assertIs<DynamicMajor.Opus?>(word.major)
            assertIs<DynamicDetail.Word>(word)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_DRAW`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/648581851972108305
            648581851972108305,
            // https://t.bilibili.com/716357878942793745
            716357878942793745,
            // https://t.bilibili.com/716358050743582725
            716358050743582725,
            // https://t.bilibili.com/716489253410832401
            716489253410832401,
            // https://t.bilibili.com/716524987542929443
            716524987542929443,
            // https://t.bilibili.com/716751027361022055
            716751027361022055,
            // https://t.bilibili.com/716752002311258165
            716752002311258165,
            // https://t.bilibili.com/718384798557536290
            718384798557536290,
            // https://t.bilibili.com/720907383182721040
            720907383182721040,
            // https://t.bilibili.com/721188862459641879
            721188862459641879,
            // https://t.bilibili.com/721282046064853080
            721282046064853080,
            // https://t.bilibili.com/721282703208480790
            721282703208480790,
            // https://t.bilibili.com/721296515797090324
            721296515797090324,
            // https://t.bilibili.com/721314095109767220
            721314095109767220
        ).forEach { id ->
            val draw = fetch(id = id)
            assertEquals(id, draw.id)
            assertIs<DynamicMajor>(draw.major)
            assertIs<DynamicDetail.Draw>(draw)
            assertFalse(draw.items.isEmpty())
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_ARTICLE`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/759447121193598993
            718372214316990512
        ).forEach { id ->
            val article = fetch(id = id)
            assertEquals(id, article.id)
            assertIs<DynamicMajor.Opus>(article.major)
            assertIs<DynamicDetail.Article>(article)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_MUSIC`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/649393566418731042
            649393566418731042
        ).forEach { id ->
            val music = fetch(id = id)
            assertEquals(id, music.id)
            assertIs<DynamicMajor.Music>(music.major)
            assertIs<DynamicDetail.Music>(music)
            assertNotNull(music.modules.dynamic.describe)
            assertNotEquals(0L, music.major.content.id)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_COMMON_SQUARE`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/451600413724453059
            451600413724453059,
            // https://t.bilibili.com/551309621391003098
            551309621391003098,
            // https://t.bilibili.com/716481612656672789
            // 716481612656672789,
            // https://t.bilibili.com/716503778995470375
            // 716503778995470375
        ).forEach { id ->
            val sketch = fetch(id = id)
            assertEquals(id, sketch.id)
            assertIs<DynamicMajor.Common>(sketch.major)
            assertIs<DynamicDetail.Sketch>(sketch)
            assertNotNull(sketch.modules.dynamic.describe)
            assertNotEquals(0L, sketch.major.content.sketchId)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_LIVE`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/216042859353895488
            216042859353895488,
            // https://t.bilibili.com/267505569812738175
            267505569812738175,
            // https://t.bilibili.com/387009689050325972
            387009689050325972
        ).forEach { id ->
            val live = fetch(id = id)
            assertEquals(id, live.id)
            assertIs<DynamicMajor.Live>(live.major)
            assertIs<DynamicDetail.Live>(live)
            assertNotEquals(0L, live.major.content.id)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_LIVE_RCMD`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/718371505648435205
            // 718371505648435205,
            // https://t.bilibili.com/741257840201564163
            741257840201564163
        ).forEach { id ->
            val room = fetch(id = id)
            assertEquals(id, room.id)
            assertIs<DynamicMajor.LiveRoom>(room.major)
            assertIs<DynamicDetail.LiveRoom>(room)
            assertNotEquals(0L, room.major.content.info.play.liveId)
            assertNotEquals(0L, room.major.content.info.play.roomId)
            assertNotEquals(0L, room.major.content.info.play.uid)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_PGC_UNION`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/610309947914189224
            610309947914189224,
            // https://t.bilibili.com/622897393256608921
            622897393256608921,
            // https://t.bilibili.com/645981661420322824
            645981661420322824,
            // https://t.bilibili.com/649955687456047124
            649955687456047124,
            // https://t.bilibili.com/759226097881579553
            759226097881579553
        ).forEach { id ->
            val episode = fetch(id = id)
            assertEquals(id, episode.id)
            assertIs<ModuleAuthor.Episode>(episode.author)
            assertIs<DynamicMajor.Episode>(episode.major)
            assertIs<DynamicDetail.Episode>(episode)
            assertNotEquals(0L, episode.major.content.episodeId)
            assertNotEquals(0L, episode.major.content.seasonId)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_UGC_SEASON`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/707069316363714560
            707069316363714560,
            // https://t.bilibili.com/716509100448415814
            716509100448415814,
            // https://t.bilibili.com/718390979031203873
            718390979031203873
        ).forEach { id ->
            val season = fetch(id = id)
            assertEquals(id, season.id)
            assertIs<ModuleAuthor.Season>(season.author)
            assertIs<DynamicMajor.Season>(season.major)
            assertIs<DynamicDetail.Season>(season)
            assertNotEquals("", season.major.content.bvid)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_MEDIALIST`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/534428265320147158
            534428265320147158
        ).forEach { id ->
            val medialist = fetch(id = id)
            assertEquals(id, medialist.id)
            assertIs<DynamicMajor.MediaList>(medialist.major)
            assertIs<DynamicDetail.MediaList>(medialist)
            assertNotEquals(0L, medialist.major.content.id)
        }
    }

    @Test
    fun `type DYNAMIC_TYPE_COURSES_SEASON`(): Unit = runBlocking {
        arrayOf(
            // https://t.bilibili.com/717906712866062340
            717906712866062340
        ).forEach { id ->
            val courses = fetch(id = id)
            assertEquals(id, courses.id)
            assertIs<DynamicMajor.Courses>(courses.major)
            assertIs<DynamicDetail.Courses>(courses)
            assertNotNull(courses.modules.dynamic.describe)
            assertNotEquals(0L, courses.major.content.id)
        }
    }
}