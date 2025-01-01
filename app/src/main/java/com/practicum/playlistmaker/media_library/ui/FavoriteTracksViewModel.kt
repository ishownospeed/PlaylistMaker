package com.practicum.playlistmaker.media_library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media_library.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private val stateLiveData = MutableStateFlow<FavoriteState>(FavoriteState.Empty)
    fun observeState(): StateFlow<FavoriteState> = stateLiveData

    init {
        viewModelScope.launch {
            favoriteTrackInteractor
                .getFavoriteTracks()
                .collect { track ->
                    processResult(track)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteState.Empty)
        } else {
            renderState(FavoriteState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteState) {
        stateLiveData.value = state
    }
}