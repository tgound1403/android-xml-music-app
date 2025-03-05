package com.tmatraining.musicapp.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tmatraining.musicapp.core.db.entity.Song

@Database(entities = [Song::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}