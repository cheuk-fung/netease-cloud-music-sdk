package ng.cloudmusic.sdk

import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.CookieJar
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import retrofit2.http.POST

internal class CloudMusicSDKTest {
    private interface Api {
        @POST
        fun valid(): Observable<JsonObject>

        fun notPost(): Observable<JsonObject>

        @POST
        fun notJsonObject(): Observable<String>

        @POST
        fun notObservable(): String
    }

    private val sdk = CloudMusicSDK(CookieJar.NO_COOKIES)
    private val api = mock(Api::class.java)
    private val errorProbedApi = sdk.errorProbe(api)

    @Test
    fun `errorProbe() - call valid method with code 200 should be OK`() {
        `when`(api.valid())
                .thenReturn(Observable.just(JsonObject().apply { addProperty("code", 200) }))
                .thenReturn(Observable.just(JsonObject().apply { addProperty("code", "200") }))

        errorProbedApi.valid().blockingSingle()
        errorProbedApi.valid().blockingSingle()

        verify(api, times(2)).valid()
    }

    @Test
    fun `errorProbe() - call valid method with other code should not be OK`() {
        `when`(api.valid())
                .thenReturn(Observable.just(JsonObject().apply { addProperty("code", 404) }))
                .thenReturn(Observable.just(JsonObject().apply { addProperty("code", "404") }))

        assertThrows<NotOK> { errorProbedApi.valid().blockingSingle() }
        assertThrows<NotOK> { errorProbedApi.valid().blockingSingle() }

        verify(api, times(2)).valid()
    }

    @Test
    fun `errorProbe() - call valid method without code should not be OK`() {
        `when`(api.valid()).thenReturn(Observable.just(JsonObject()))
        assertThrows<NotOK> { errorProbedApi.valid().blockingSingle() }
        verify(api).valid()
    }

    @Test
    fun `errorProbe() - not post method should not be probed`() {
        `when`(api.notPost()).thenReturn(Observable.just(JsonObject()))
        errorProbedApi.notPost()
        verify(api).notPost()
    }

    @Test
    fun `errorProbe() - method not returning JsonObject should not be probed`() {
        `when`(api.notJsonObject()).thenReturn(Observable.just(""))
        errorProbedApi.notJsonObject()
        verify(api).notJsonObject()
    }

    @Test
    fun `errorProbe() - method not returning Observable should not be probed`() {
        `when`(api.notObservable()).thenReturn("")
        errorProbedApi.notObservable()
        verify(api).notObservable()
    }

    @Test
    fun `errorProbe() - toString(), hashCode(), equals() is not proxied`() {
        assertThat(errorProbedApi.toString()).isNotNull()
        assertThat(errorProbedApi.hashCode()).isNotNull()
        assertThat(errorProbedApi).isEqualTo(errorProbedApi)
        assertThat(errorProbedApi).isNotEqualTo(api)
        verifyZeroInteractions(api)
    }
}
