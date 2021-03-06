package ng.cloudmusic.sdk.service

import io.reactivex.Single
import ng.cloudmusic.api.LoginApi
import ng.cloudmusic.util.getAs
import org.apache.commons.codec.digest.DigestUtils

class LoginService internal constructor(private val loginApi: LoginApi) {
    fun loginByCellphone(phone: String, password: String): Single<Profile> =
            loginApi.loginByCellphone(LoginApi.LoginByCellphoneRequest(phone, DigestUtils.md5Hex(password)))
                    .map { it["profile"].getAs<Profile>() }

    data class Profile(val nickname: String)
}
