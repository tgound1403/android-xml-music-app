package com.tmatraining.musicapp.core.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.tmatraining.musicapp.core.db.entity.Song
import java.io.File

object FileUtil {
    fun isAudioFile(file: File): Boolean {
        val audioExtensions = listOf(".mp3", ".wav", ".m4a", ".aac", ".flac")
        return audioExtensions.any {
            file.extension.equals(
                it.removePrefix("."),
                ignoreCase = true
            )
        }
    }

    fun createSongFromFile(file: File, context: Context): Song {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)

        val id = file.hashCode().toLong()
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            ?: file.nameWithoutExtension
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            ?: "Unknown Artist"
        val album =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"
        val duration =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val uri = Uri.fromFile(file)
        val albumArtUri = getAlbumArtUri(file, context)

        retriever.release()

        return Song(
            id, uri.toString(), title, artist, album, duration = duration, "",
            albumArtUri.toString()
        )
    }

    private fun getAlbumArtUri(file: File, context: Context): Uri? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val embeddedPicture = retriever.embeddedPicture
        retriever.release()

        return if (embeddedPicture != null) {
            val tempFile = File(context.cacheDir, "album_art_${file.name}.jpg")
            tempFile.writeBytes(embeddedPicture)
            Uri.fromFile(tempFile)
        } else {
            null
        }
    }
}