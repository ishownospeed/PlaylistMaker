package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun saveTrackInHistory(item: Track)
    fun getSearchHistory(): MutableList<Track>
    fun clearListSearchHistory()
}