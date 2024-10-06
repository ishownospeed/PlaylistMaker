package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

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