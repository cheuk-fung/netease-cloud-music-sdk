package ng.cloudmusic.api

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("login/cellphone")
    fun loginByCellphone(@Body credential: Credential): Observable<String>

    data class Credential(val phone: String, val password: String, val rememberLogin: String = "true")
}
