package com.tmatraining.musicapp.data.datasource.local

import android.content.Context
import android.os.Environment
import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.utils.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    suspend fun getSongsFromDownloads(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (downloadsDir.exists() && downloadsDir.isDirectory) {
            downloadsDir.listFiles()?.forEach { file ->
                if (file.isFile && FileUtil.isAudioFile(file)) {
                    val song = FileUtil.createSongFromFile(file, context)
                    songs.add(song)
                }
            }
        }
        songs
    }

    suspend fun getArtists(): List<Artist> = withContext(Dispatchers.IO) {
        val artists = mutableListOf<Artist>()
        val artistNames = mutableSetOf<String>()
        getSongsFromDownloads().forEach { song ->
            val artist = Artist(name = song.artist, avatar = song.albumArtUri)
            if (!artists.contains(artist) && !artistNames.contains(artist.name)) {
                artists.add(artist)
                artistNames.add(artist.name)
            }
        }
        artists
    }

    suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val albumNames = mutableSetOf<String>()

        getSongsFromDownloads().forEach { song ->
            val album = Album(name = song.album, cover = song.albumArtUri, artist = song.artist)
            if (!album.name.contains("Single") && !albums.contains(album) && !albumNames.contains(
                    album.name
                )
            ) {
                albums.add(album)
                albumNames.add(album.name)
            }
        }
        albums
    }

    suspend fun getPlaylists(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val albumNames = mutableSetOf<String>()

        getSongsFromDownloads().forEach { song ->
            val album = Album(name = song.album, cover = song.albumArtUri, artist = song.artist)
            if (!albums.contains(album) && !albumNames.contains(album.name)) {
                albums.add(album)
                albumNames.add(album.name)
            }
        }
        albums
    }

    suspend fun getSongByArtist(artistName: String): List<Song> {
        return getSongsFromDownloads().filter { it.artist == artistName }
    }

    suspend fun getSongByAlbum(albumName: String): List<Song> {
        return getSongsFromDownloads().filter { it.album == albumName }
    }
}