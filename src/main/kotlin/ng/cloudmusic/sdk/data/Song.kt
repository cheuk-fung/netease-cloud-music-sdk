package ng.cloudmusic.sdk.data

import com.google.gson.annotations.SerializedName

data class Song(
        val id: Int,
        val name: String,
        val artists: List<Artist>,
        val album: Album,
        val starred: Boolean
)

data class Artist(
        val id: Int,
        val name: String,
        val picUrl: String
)

data class Album(
        val id: Int,
        val name: String,
        val picUrl: String,
        val artists: List<Artist>
)

data class SongFile(
        val id: Int,
        val url: String,
        @SerializedName("br") val bitRate: Int,
        val size: Int,
        val md5: String,
        val type: String
)

enum class SongQuality(val bitRate: Int) {
        STANDARD(128000),
        MEDIUM(192000),
        HIGH(320000)
}
