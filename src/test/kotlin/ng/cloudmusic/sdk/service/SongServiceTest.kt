package ng.cloudmusic.sdk.service

import com.google.gson.JsonObject
import io.reactivex.Single
import ng.cloudmusic.api.SongApi
import ng.cloudmusic.helper.mockito.any
import ng.cloudmusic.sdk.data.SongQuality
import ng.cloudmusic.util.gson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

private class SongServiceTest {
    private val songApi = mock(SongApi::class.java)
    private val songService = SongService(songApi)

    @Test
    fun `songFile() - when song api returns empty data, it should return empty`() {
        val json = """
            {
                "data": [
                ]
            }
        """.trimIndent()
        `when`(songApi.songFile(any())).thenReturn(response(json))

        assertThat(songService.songFile(0, SongQuality.HIGH).blockingGet()).isNull()
    }

    @Test
    fun `songFile() - when song api returns data with non-200 code, it should return empty`() {
        val json = """
            {
                "data": [
                    {
                        "code": 404
                    }
                ]
            }
        """.trimIndent()
        `when`(songApi.songFile(any())).thenReturn(response(json))

        assertThat(songService.songFile(0, SongQuality.HIGH).blockingGet()).isNull()
    }

    @Test
    fun `songFile() - when song api returns valid data, it should return valid SongFile`() {
        val json = """
            {
                "data": [
                    {
                        "id": 1,
                        "url": "url-to-be-verify",
                        "code": 200
                    }
                ]
            }
        """.trimIndent()
        `when`(songApi.songFile(any())).thenReturn(response(json))

        songService.songFile(1, SongQuality.HIGH).blockingGet()
                .apply {
                    assertThat(id).isEqualTo(1)
                    assertThat(url).isEqualTo("url-to-be-verify")
                }
    }

    @Test
    fun `songFile() - when song api returns multiple data, it should return only one SongFile`() {
        val json = """
            {
                "data": [
                    {
                        "code": 404
                    },
                    {
                        "id": 1,
                        "br": 128000,
                        "code": 200
                    },
                    {
                        "id": 1,
                        "br": 192000,
                        "code": 200
                    }
                ]
            }
        """.trimIndent()
        `when`(songApi.songFile(any())).thenReturn(response(json))

        songService.songFile(1, SongQuality.HIGH).blockingGet()
                .apply { assertThat(id).isEqualTo(1) }
    }

    private fun response(json: String): Single<JsonObject> = Single.just(gson.fromJson(json, JsonObject::class.java))
}
