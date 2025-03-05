package com.tmatraining.musicapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.domain.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: SongRepository) : ViewModel() {
    private val _tracks = MutableLiveData<List<Song>>()
    val tracks: LiveData<List<Song>> get() = _tracks
    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>> get() = _artists
    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums
    private val _playlists = MutableLiveData<List<Album>>()
    val playlists: LiveData<List<Album>> get() = _playlists
    private val _songsByFactor = MutableLiveData<List<Song>>()
    val songsByFactor: LiveData<List<Song>> get() = _songsByFactor

    fun fetchSongOnDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getAllSongs()
            withContext(Dispatchers.Main) { _tracks.value = data }
        }
    }

    fun getArtists() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getArtists()
            withContext(Dispatchers.Main) {
                _artists.value = data
            }
        }
    }

    fun getAlbums() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getAlbums()
            withContext(Dispatchers.Main) {
                _albums.value = data
            }
        }
    }

    fun getPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getPlaylists()
            withContext(Dispatchers.Main) {
                _playlists.value = data
            }
        }
    }

    fun getSongByArtist(artistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getSongByArtist(artistName)
            withContext(Dispatchers.Main) {
                _songsByFactor.value = data
            }
        }
    }

    fun getSongByAlbum(albumName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getSongByAlbum(albumName)
            _songsByFactor.value = data
        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}