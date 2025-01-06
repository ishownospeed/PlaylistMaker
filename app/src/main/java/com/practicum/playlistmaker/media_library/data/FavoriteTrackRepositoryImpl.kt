package com.practicum.playlistmaker.media_library.data

import com.practicum.playlistmaker.media_library.data.db.AppDatabase
import com.practicum.playlistmaker.media_library.data.mapping.TrackMapping
import com.practicum.playlistmaker.media_library.domain.api.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val mapping: TrackMapping,
) : FavoriteTrackRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = mapping.map(track)
        appDatabase.favoriteTracksDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = mapping.map(track)
        appDatabase.favoriteTracksDao().deleteTrack(trackEntity)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        val tracks = appDatabase.favoriteTracksDao().getFavoriteList()
        return (tracks.map { track -> track.map { mapping.map(it) } })
    }

    override suspend fun getFavoriteIdList(): List<Int> {
        return appDatabase.favoriteTracksDao().getFavoriteIdList()
    }
}