package ng.cloudmusic.sdk.service

import com.google.gson.JsonObject
import io.reactivex.Observable
import ng.cloudmusic.api.RadioApi

class RadioService internal constructor(private val radioApi: RadioApi) {
    fun personalFM(): Observable<JsonObject> {
        return radioApi.personalFM()
    }
}
