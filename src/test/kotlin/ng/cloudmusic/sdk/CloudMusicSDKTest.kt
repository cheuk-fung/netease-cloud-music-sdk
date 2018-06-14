package ng.cloudmusic.sdk

import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.CookieJar
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.skyscreamer.jsonassert.JSONAssert
import retrofit2.http.POST

internal class CloudMusicSDKTest {
    internal interface Api {
        @POST
        fun valid(): Single<JsonObject>

        fun notPost(): Single<JsonObject>

        @POST
        fun notJsonObject(): Single<String>

        @POST
        fun notSingle(): String
    }

    private val sdk = CloudMusicSDK(CookieJar.NO_COOKIES)
    private val api = mock(Api::class.java)
    private val errorProbedApi = sdk.errorProbe(api)

    @Test
    fun `errorProbe() - call valid method with code 200 should be OK`() {
        `when`(api.valid())
                .thenReturn(Single.just(JsonObject().apply { addProperty("code", 200) }))
                .thenReturn(Single.just(JsonObject().apply { addProperty("code", "200") }))

        errorProbedApi.valid().blockingGet()
                .also { JSONAssert.assertEquals(it.toString(), """{"code": 200}""", true) }
        errorProbedApi.valid().blockingGet()
                .also { JSONAssert.assertEquals(it.toString(), """{"code": "200"}""", true) }

        verify(api, times(2)).valid()
    }

    @Test
    fun `errorProbe() - call valid method with other code should not be OK`() {
        `when`(api.valid())
                .thenReturn(Single.just(JsonObject().apply {
                    addProperty("code", 404)
                    addProperty("msg", "first time")
                }))
                .thenReturn(Single.just(JsonObject().apply {
                    addProperty("code", "404")
                    addProperty("msg", "second time")
                }))

        assertThrows<NotOK> { errorProbedApi.valid().blockingGet() }
                .errorResponse
                .also {
                    assertThat(it.code).isEqualTo(404)
                    assertThat(it.msg).isEqualTo("first time")
                }

        assertThrows<NotOK> { errorProbedApi.valid().blockingGet() }
                .errorResponse
                .also {
                    assertThat(it.code).isEqualTo(404)
                    assertThat(it.msg).isEqualTo("second time")
                }

        verify(api, times(2)).valid()
    }

    @Test
    fun `errorProbe() - call valid method without code should not be OK`() {
        `when`(api.valid()).thenReturn(Single.just(JsonObject().apply { addProperty("msg", "no code") }))

        assertThrows<NotOK> { errorProbedApi.valid().blockingGet() }
                .errorResponse
                .also {
                    assertThat(it.code).isEqualTo(0)
                    assertThat(it.msg).isEqualTo("no code")
                }

        verify(api).valid()
    }

    @Test
    fun `errorProbe() - not post method should not be probed`() {
        `when`(api.notPost()).thenReturn(Single.just(JsonObject().apply { addProperty("msg", "not post") }))

        errorProbedApi.notPost().blockingGet()
                .let { JSONAssert.assertEquals(it.toString(), """{"msg": "not post"}""", true) }

        verify(api).notPost()
    }

    @Test
    fun `errorProbe() - method not returning JsonObject should not be probed`() {
        `when`(api.notJsonObject()).thenReturn(Single.just("not JsonObject"))

        errorProbedApi.notJsonObject().blockingGet()
                .also { assertThat(it).isEqualTo("not JsonObject") }

        verify(api).notJsonObject()
    }

    @Test
    fun `errorProbe() - method not returning Single should not be probed`() {
        `when`(api.notSingle()).thenReturn("not Single")

        errorProbedApi.notSingle()
                .also { assertThat(it).isEqualTo("not Single") }

        verify(api).notSingle()
    }

    @Test
    fun `errorProbe() - toString(), hashCode(), equals() is not proxied`() {
        assertThat(errorProbedApi.toString()).isNotEqualTo(api.toString())
        assertThat(errorProbedApi.hashCode()).isNotEqualTo(api.hashCode())
        assertThat(errorProbedApi).isEqualTo(errorProbedApi)
        assertThat(errorProbedApi).isNotEqualTo(api)
        verifyZeroInteractions(api)
    }
}
