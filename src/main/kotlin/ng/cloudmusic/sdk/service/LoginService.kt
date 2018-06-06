package ng.cloudmusic.sdk.service

import io.reactivex.Observable
import ng.cloudmusic.api.LoginApi
import org.apache.commons.codec.digest.DigestUtils

class LoginService(private val loginApi: LoginApi) {
    fun loginByCellphone(phone: String, password: String): Observable<String> {
        val credential = LoginApi.Credential(phone, DigestUtils.md5Hex(password))
        return loginApi.loginByCellphone(credential)
    }
}
