package ng.cloudmusic.api

import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.POST

interface RadioApi {
    @POST("v1/radio/get")
    fun personalFM(): Observable<JsonObject>
}
