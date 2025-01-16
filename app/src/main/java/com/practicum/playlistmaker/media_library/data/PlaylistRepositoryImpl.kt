package com.practicum.playlistmaker.media_library.data

import com.practicum.playlistmaker.media_library.data.db.AppDatabase
import com.practicum.playlistmaker.media_library.data.mapping.PlaylistMapping
import com.practicum.playlistmaker.media_library.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistMapping: PlaylistMapping
) : PlaylistRepository {

    override suspend fun addNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistMapping.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getListPlaylists(): Flow<List<Playlist>> {
        val list = appDatabase.playlistDao().getListPlaylists()
        return list.map { play -> play.map { playlistMapping.map(it) } }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        appDatabase.playlistDao().insertPlaylist(playlistMapping.map(playlist))
    }

}