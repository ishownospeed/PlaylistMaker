package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun onResponse(foundTracks: List<Track>)
        fun onFailure(t: Throwable)
    }
}