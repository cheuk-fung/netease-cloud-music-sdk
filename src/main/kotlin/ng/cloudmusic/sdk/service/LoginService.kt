package ng.cloudmusic.sdk.service

import com.google.gson.Gson
import io.reactivex.Observable
import ng.cloudmusic.api.LoginApi
import org.apache.commons.codec.digest.DigestUtils

class LoginService(private val loginApi: LoginApi) {
    private val gson = Gson()

    fun loginByCellphone(phone: String, password: String): Observable<Profile> {
        val credential = LoginApi.Credential(phone, DigestUtils.md5Hex(password))
        return loginApi.loginByCellphone(credential)
                .map {
                    if (it["code"].asInt != 200) {
                        // TODO specific type of exception
                        throw RuntimeException(it.toString())
                    }
                    gson.fromJson(it["profile"], Profile::class.java)
                }
    }

    data class Profile(val nickname: String)
}
