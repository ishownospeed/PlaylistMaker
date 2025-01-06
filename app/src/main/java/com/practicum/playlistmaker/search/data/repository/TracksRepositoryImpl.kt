package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.media_library.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.Resource
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {
        val response: Response = networkClient.doRequest(TracksSearchRequest(text))

        when (response.resultCode) {
            -1 -> emit(Resource.Error("Проверьте подключение к интернету"))

            SUCCESS_OK -> {
                val favoriteTrackIds = appDatabase.favoriteTracksDao().getFavoriteIdList()

                val data = Resource.Success((response as TracksSearchResponse).results.map { trackDto ->
                    val isFavorite = trackDto.trackId in favoriteTrackIds

                    Track(
                        trackId = trackDto.trackId,
                        trackName = trackDto.trackName,
                        artistName = trackDto.artistName,
                        trackTimeMillis = DateTimeUtil.simpleFormatTrack(trackDto.trackTimeMillis),
                        artworkUrl100 = trackDto.artworkUrl100,
                        collectionName = trackDto.collectionName,
                        releaseDate = trackDto.releaseDate,
                        primaryGenreName = trackDto.primaryGenreName,
                        country = trackDto.country,
                        previewUrl = trackDto.previewUrl,
                        isFavorite = isFavorite
                    )
                })
                emit(data)
            }

            else -> emit(Resource.Error("Ошибка сервера"))
        }
    }

    companion object {
        private const val SUCCESS_OK = 200
    }

}