package ng.cloudmusic.sdk.data

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
