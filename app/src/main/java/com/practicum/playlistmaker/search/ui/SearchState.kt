package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    data object Loading : SearchState
    data class SearchList(val tracks: List<Track>) : SearchState
    data class HistoryList(val tracks: List<Track>) : SearchState
    data object Empty : SearchState
    data object Error : SearchState
}