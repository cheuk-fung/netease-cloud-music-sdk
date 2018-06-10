package ng.cloudmusic.api

import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

internal interface LoginApi {
    @POST("login/cellphone")
    fun loginByCellphone(@Body credential: Credential): Observable<JsonObject>

    data class Credential(val phone: String, val password: String, val rememberLogin: String = "true")
}
