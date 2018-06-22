package ng.cloudmusic.sdk.service

import io.reactivex.Observable
import ng.cloudmusic.api.RadioApi
import ng.cloudmusic.sdk.data.Song
import ng.cloudmusic.util.getAs

class RadioService internal constructor(private val radioApi: RadioApi) {
    fun personalFM(): Observable<Song> = radioApi.personalFM()
            .flattenAsObservable { it["data"].getAs<List<Song>>() }
}
