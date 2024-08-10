package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun saveTrackInHistory(item: Track) {
        repository.saveTrackInHistory(item)
    }

    override fun getSearchHistory(): MutableList<Track> {
        return repository.getSearchHistory()
    }

    override fun clearListSearchHistory() {
        repository.clearListSearchHistory()
    }

}