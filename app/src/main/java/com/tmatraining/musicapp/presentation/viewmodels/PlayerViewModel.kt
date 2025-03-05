package com.tmatraining.musicapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.services.MusicService
import com.tmatraining.musicapp.core.services.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor() : ViewModel() {
    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private var musicService: MusicService? = null

    fun setMusicService(service: MusicService) {
        musicService = service
    }

    fun playSong(song: Song) {
        musicService?.startPlaying(song)
    }

    fun togglePlayPause() {
        val isPlaying = PlaybackState.isPlaying.value ?: false
        if (isPlaying) {
            musicService?.pause()
        } else {
            musicService?.continuePlay() ?: PlaybackState.currentSong.value?.let { playSong(it) }
        }
    }

    fun seekTo(position: Int) {
        musicService?.seekTo(position)
    }

    fun updatePosition() {
        viewModelScope.launch {
            while (true) {
                if (PlaybackState.isPlaying.value == true) {
                    val position = musicService?.getCurrentPosition() ?: 0
                    _currentPosition.postValue(position)
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}