package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun saveTrackInHistory(item: Track)
    fun getSearchHistory(): MutableList<Track>
    fun createListTracksFromJson(json: String): MutableList<Track>
    fun createJsonFromListTracks(list: MutableList<Track>): String
}