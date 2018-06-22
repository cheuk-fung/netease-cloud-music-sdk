package ng.cloudmusic.api

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

internal interface SongApi {
    @POST("song/enhance/player/url")
    fun songFile(@Body request: SongFileRequest): Single<JsonObject>

    data class SongFileRequest(val ids: List<Int>, val br: Int)
}
