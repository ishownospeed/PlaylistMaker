package com.practicum.playlistmaker.media_library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media_library.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val stateLiveData = MutableStateFlow<ListPlaylistState>(ListPlaylistState.Empty)
    fun observeState(): StateFlow<ListPlaylistState> = stateLiveData

    init {
        viewModelScope.launch {
            playlistInteractor
                .getListPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(ListPlaylistState.Empty)
        } else {
            renderState(ListPlaylistState.Content(playlists))
        }
    }

    private fun renderState(state: ListPlaylistState) {
        stateLiveData.value = state
    }

    sealed interface ListPlaylistState {
        data object Empty : ListPlaylistState
        data class Content(val playlists: List<Playlist>) : ListPlaylistState
    }
}