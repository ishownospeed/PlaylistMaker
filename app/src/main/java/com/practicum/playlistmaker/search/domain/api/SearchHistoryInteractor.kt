package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun saveTrackInHistory(item: Track)
    fun getSearchHistory(): MutableList<Track>
    fun clearListSearchHistory()
}