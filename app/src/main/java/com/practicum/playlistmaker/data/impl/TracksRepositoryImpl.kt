package com.practicum.playlistmaker.data.impl

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(text: String): List<Track> {
        val response: Response = networkClient.doRequest(TracksSearchRequest(text))

        return if (response.resultCode == SUCCESS_OK) {
            (response as TracksSearchResponse).results.map { trackDto ->
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
                    previewUrl = trackDto.previewUrl
                )
            }
        } else {
            emptyList()
        }
    }

    companion object {
        private const val SUCCESS_OK = 200
    }

}