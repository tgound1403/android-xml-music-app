package com.tmatraining.musicapp.data.datasource.local

import android.content.Context
import android.os.Environment
import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.utils.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SongDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    fun getSongsFromDownloads(): List<Song> {
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
        return songs
    }

    fun getArtists(): List<Artist>  {
        val artists = mutableListOf<Artist>()
        val artistNames = mutableSetOf<String>()
        getSongsFromDownloads().forEach { song ->
            val artist = Artist(name = song.artist, avatar = song.albumArtUri)
            if (!artists.contains(artist) && !artistNames.contains(artist.name)) {
                artists.add(artist)
                artistNames.add(artist.name)
            }
        }
        return artists
    }

    fun getAlbums(): List<Album> {
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
        return albums
    }

    fun getPlaylists(): List<Album> {
        val albums = mutableListOf<Album>()
        val albumNames = mutableSetOf<String>()

        getSongsFromDownloads().forEach { song ->
            val album = Album(name = song.album, cover = song.albumArtUri, artist = song.artist)
            if (!albums.contains(album) && !albumNames.contains(album.name)) {
                albums.add(album)
                albumNames.add(album.name)
            }
        }
        return albums
    }

    fun getSongByArtist(artistName: String): List<Song> {
        return getSongsFromDownloads().filter { it.artist == artistName }
    }

    fun getSongByAlbum(albumName: String): List<Song> {
        return getSongsFromDownloads().filter { it.album == albumName }
    }
}