package ng.cloudmusic.api

import io.reactivex.Observable
import retrofit2.http.POST

interface LoginApi {

    @POST("login/cellphone")
    fun loginByCellphone(): Observable<String>

    companion object {
        fun create(): LoginApi {
            return retrofit.create(LoginApi::class.java)
        }
    }
}
