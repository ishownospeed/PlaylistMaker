package com.practicum.playlistmaker.media_library.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteState {

    data object Empty : FavoriteState

    data class Content(
        val tracks: List<Track>
    ) : FavoriteState
}