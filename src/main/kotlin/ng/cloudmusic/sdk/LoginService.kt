package ng.cloudmusic.sdk

import io.reactivex.Observable
import ng.cloudmusic.api.LoginApi

object LoginService {

    private val loginApi = LoginApi.create()

    fun loginByCellphone(): Observable<String> {
        return loginApi.loginByCellphone()
    }
}
