package com.tmatraining.musicapp.core.extensions

import android.content.ContentUris
import android.net.Uri

fun getAlbumArtUri(albumId: Long): Uri {
    return ContentUris.withAppendedId(
        Uri.parse("content://media/external/audio/albumart"),
        albumId
    )
}