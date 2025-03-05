package com.tmatraining.musicapp.domain

import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.core.db.entity.Song

interface SongRepository {
    suspend fun getAllSongs(): List<Song>

    suspend fun getArtists(): List<Artist>

    suspend fun getAlbums(): List<Album>

    suspend fun getPlaylists(): List<Album>

    suspend fun getSongByArtist(artistName: String): List<Song>

    suspend fun getSongByAlbum(albumName: String): List<Song>
}