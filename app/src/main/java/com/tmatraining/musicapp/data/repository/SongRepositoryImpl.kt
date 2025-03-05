package com.tmatraining.musicapp.data.repository

import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.data.datasource.local.SongDataSource
import com.tmatraining.musicapp.data.datasource.local.SongLocalDataSource
import com.tmatraining.musicapp.domain.SongRepository
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val songDataSource: SongDataSource,
    private val songLocalDataSource: SongLocalDataSource
) : SongRepository {

    override suspend fun getAllSongs(): List<Song> {
        val data = songDataSource.getSongsFromDownloads()
        songLocalDataSource.insertAll(data)
        return songLocalDataSource.getAllSongs()
    }

    override suspend fun getArtists(): List<Artist> {
        return songDataSource.getArtists()
    }

    override suspend fun getAlbums(): List<Album> {
        return songDataSource.getAlbums()
    }

    override suspend fun getPlaylists(): List<Album> {
        return songDataSource.getPlaylists()
    }

    override suspend fun getSongByArtist(artistName: String): List<Song> {
        return songDataSource.getSongByArtist(artistName)
    }

    override suspend fun getSongByAlbum(albumName: String): List<Song> {
        return songDataSource.getSongByAlbum(albumName)
    }
}