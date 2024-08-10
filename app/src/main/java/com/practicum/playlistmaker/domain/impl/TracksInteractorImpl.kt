package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {
        val t = Thread {
            try {
                consumer.onResponse(repository.searchTracks(text))
            } catch (throwable: Throwable) {
                consumer.onFailure(throwable)
            }
        }
        t.start()
    }

}