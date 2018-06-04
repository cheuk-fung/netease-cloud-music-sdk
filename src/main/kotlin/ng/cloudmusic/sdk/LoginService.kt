package ng.cloudmusic.sdk

import io.reactivex.Observable
import ng.cloudmusic.api.LoginApi
import org.apache.commons.codec.digest.DigestUtils

object LoginService {

    private val loginApi = LoginApi.create()

    fun loginByCellphone(phone: String, password: String): Observable<String> {
        val credential = LoginApi.Credential(phone, DigestUtils.md5Hex(password))
        return loginApi.loginByCellphone(credential)
    }
}
