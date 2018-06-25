package ng.cloudmusic.sdk.service

import io.reactivex.Maybe
import ng.cloudmusic.api.SongApi
import ng.cloudmusic.sdk.data.SongFile
import ng.cloudmusic.sdk.data.SongQuality
import ng.cloudmusic.util.getAs

class SongService internal constructor(private val songApi: SongApi) {
    fun songFile(id: Int, quality: SongQuality): Maybe<SongFile> =
            songApi.songFile(SongApi.SongFileRequest(listOf(id), quality.bitRate))
                    .flatMapMaybe {
                        it["data"].asJsonArray
                                .firstOrNull { it.asJsonObject["code"].asInt == 200 }
                                ?.getAs<SongFile>()
                                ?.let { Maybe.just(it) }
                                ?: Maybe.empty()
                    }
}
