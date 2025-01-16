package com.practicum.playlistmaker.media_library.domain.impl

import com.practicum.playlistmaker.media_library.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.media_library.domain.api.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : FavoriteTrackInteractor {

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTrackRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTrackRepository.removeTrackFromFavorites(track)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks()
    }

    override suspend fun getFavoriteIdList(): List<Long> {
        return favoriteTrackRepository.getFavoriteIdList()
    }
}