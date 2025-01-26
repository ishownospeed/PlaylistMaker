package com.practicum.playlistmaker.media_library.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media_library.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media_library.domain.api.SaveImageStorageInteractor
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val saveImageStorageInteractor: SaveImageStorageInteractor
) : ViewModel() {

    fun addNewPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addNewPlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    fun saveImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        viewModelScope.launch {
            val savedUri = saveImageStorageInteractor.saveImageToPrivateStorage(uri)
            onSuccess(savedUri)
        }
    }

}