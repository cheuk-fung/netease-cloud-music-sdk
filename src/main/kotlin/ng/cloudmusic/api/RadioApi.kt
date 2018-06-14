package ng.cloudmusic.api

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.POST

internal interface RadioApi {
    @POST("v1/radio/get")
    fun personalFM(): Single<JsonObject>
}
