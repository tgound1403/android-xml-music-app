package com.tmatraining.musicapp.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Long = 0,
    val filePath: String = "",
    val albumArtUri: String = ""
)