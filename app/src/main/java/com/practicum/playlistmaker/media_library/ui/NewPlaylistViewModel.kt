package com.practicum.playlistmaker.media_library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media_library.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun addNewPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addNewPlaylist(playlist)
        }
    }

}