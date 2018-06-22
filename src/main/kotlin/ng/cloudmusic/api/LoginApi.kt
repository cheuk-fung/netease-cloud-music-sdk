package ng.cloudmusic.api

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

internal interface LoginApi {
    @POST("login/cellphone")
    fun loginByCellphone(@Body loginByCellphoneRequest: LoginByCellphoneRequest): Single<JsonObject>

    data class LoginByCellphoneRequest(val phone: String, val password: String, val rememberLogin: String = "true")
}
