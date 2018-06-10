package ng.cloudmusic.sdk.service

import io.reactivex.Observable
import ng.cloudmusic.api.LoginApi
import ng.cloudmusic.util.gson
import org.apache.commons.codec.digest.DigestUtils

class LoginService(private val loginApi: LoginApi) {
    fun loginByCellphone(phone: String, password: String): Observable<Profile> {
        val credential = LoginApi.Credential(phone, DigestUtils.md5Hex(password))
        return loginApi.loginByCellphone(credential)
                .map { gson.fromJson(it["profile"], Profile::class.java) }
    }

    data class Profile(val nickname: String)
}
