package com.tmatraining.musicapp.data.datasource.local

import com.tmatraining.musicapp.core.db.SongDao
import com.tmatraining.musicapp.core.db.entity.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongLocalDataSource @Inject constructor(private val songDao: SongDao) {
    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        return@withContext songDao.getAllSongs()
    }

    suspend fun insertAll(songs: List<Song>) = withContext(Dispatchers.IO) {
        songs.map{ song -> songDao.insertAll(song) }
    }
}