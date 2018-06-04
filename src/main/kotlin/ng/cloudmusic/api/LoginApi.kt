package ng.cloudmusic.api

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    data class Credential(val phone: String, val password: String, val rememberLogin: String = "true")

    @POST("login/cellphone")
    fun loginByCellphone(@Body credential: Credential): Observable<String>

    companion object {
        fun create(): LoginApi {
            return retrofit.create(LoginApi::class.java)
        }
    }
}
