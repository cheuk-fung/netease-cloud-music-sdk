package ng.cloudmusic.sdk.service

import io.reactivex.Observable
import ng.cloudmusic.api.LoginApi
import ng.cloudmusic.util.getAs
import org.apache.commons.codec.digest.DigestUtils

class LoginService internal constructor(private val loginApi: LoginApi) {
    fun loginByCellphone(phone: String, password: String): Observable<Profile> {
        val credential = LoginApi.Credential(phone, DigestUtils.md5Hex(password))
        return loginApi.loginByCellphone(credential)
                .map { it["profile"].getAs<Profile>() }
    }

    data class Profile(val nickname: String)
}
