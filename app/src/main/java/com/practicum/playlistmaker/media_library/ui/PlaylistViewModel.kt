package com.practicum.playlistmaker.media_library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media_library.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    fun playlist(): LiveData<Playlist> = _playlist

    private val _tracks = MutableStateFlow<TracksState>(TracksState.Loading)
    fun tracks(): StateFlow<TracksState> = _tracks

    sealed interface TracksState {
        data object Empty : TracksState
        data object Loading : TracksState
        data class Content(val tracks: List<Track>) : TracksState
    }

    fun loadPlaylistById(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _playlist.postValue(playlist)
            getTracks(playlist.listIdsTracks)
        }
    }

    private fun getTracks(trackIds: List<Long>) {
        viewModelScope.launch {
            playlistInteractor.getTracks(trackIds).collect {
                processResult(it)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(TracksState.Empty)
        } else {
            renderState(TracksState.Content(tracks))
        }
    }

    private fun renderState(state: TracksState) {
        _tracks.value = state
    }

    fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(trackId, playlistId)
            loadPlaylistById(playlistId)
        }
    }

    fun removePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

}