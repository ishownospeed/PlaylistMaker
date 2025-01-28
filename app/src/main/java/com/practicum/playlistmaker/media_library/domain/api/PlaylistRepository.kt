package com.practicum.playlistmaker.media_library.domain.api

import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addNewPlaylist(playlist: Playlist)
    fun getListPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    suspend fun getPlaylistById(playlistId: Long): Playlist
    fun getTracks(ids: List<Long>): Flow<List<Track>>
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)

}