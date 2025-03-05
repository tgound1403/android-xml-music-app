package com.tmatraining.musicapp.data.datasource.local

import com.tmatraining.musicapp.core.db.SongDao
import com.tmatraining.musicapp.core.db.entity.Song
import javax.inject.Inject

class SongLocalDataSource @Inject constructor(private val songDao: SongDao) {
    fun getAllSongs(): List<Song> {
        return songDao.getAllSongs()
    }

    fun insertAll(songs: List<Song>) {
        songs.map{ song -> songDao.insertAll(song) }
    }
}