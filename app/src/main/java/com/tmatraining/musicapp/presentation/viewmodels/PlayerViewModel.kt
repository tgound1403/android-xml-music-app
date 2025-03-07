package com.tmatraining.musicapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.services.MusicService
import com.tmatraining.musicapp.core.services.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor() : ViewModel() {
    private var musicService: MusicService? = null

    fun setMusicService(service: MusicService) {
        musicService = service
        updateCurrentSong()
        updatePlayingState()
    }

    fun continuePlay() {
        musicService?.let {
            it.continuePlay()
            updatePlayingState()
        }
    }

    fun togglePlayPause() {
        musicService?.let {
            if (PlaybackState.isPlaying.value == true) {
                it.pause()
            } else {
                it.continuePlay()
            }
            updatePlayingState()
        }
    }

    fun pause() {
        musicService?.pause()
        updatePlayingState()
    }

    fun playNext() {
        musicService?.playNext()
        updateCurrentSong()
        updatePlayingState()
    }

    fun playPrev() {
        musicService?.playPrev()
        updateCurrentSong()
        updatePlayingState()
    }

    fun playSong(song: Song) {
        musicService?.startPlaying(song)
        updateCurrentSong()
        updatePlayingState()
    }

    fun seekTo(position: Int) {
        musicService?.seekTo(position)
    }

    fun initialize() {
        val currentSong = PlaybackState.currentSong.value
        val currentList = PlaybackState.currentList.value

        musicService?.let {
            when {
                currentSong != null && PlaybackState.isPlaying.value == true -> continuePlay()
                currentSong != null && PlaybackState.isPlaying.value != true -> playSong(currentSong)
                currentList?.isNotEmpty() == true && currentSong == null -> playSong(currentList[0])
            }
        }
    }

    private fun updateCurrentSong() {
        musicService?.getCurrentSong()?.let {
            PlaybackState.currentSong.value = it
        }
    }

    private fun updatePlayingState() {
        PlaybackState.isPlaying.value = musicService?.isPlaying() ?: false
    }

    override fun onCleared() {
        super.onCleared()
        musicService = null
    }
}