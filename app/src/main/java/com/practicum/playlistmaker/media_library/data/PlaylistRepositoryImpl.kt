package com.practicum.playlistmaker.media_library.data

import com.practicum.playlistmaker.media_library.data.db.AppDatabase
import com.practicum.playlistmaker.media_library.data.mapping.PlaylistMapping
import com.practicum.playlistmaker.media_library.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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
        val updatedPlaylist = playlist.copy(
            listIdsTracks = playlist.listIdsTracks + listOf(track.trackId),
            countTracks = playlist.listIdsTracks.size + 1
        )
        appDatabase.playlistDao().insertPlaylist(playlistMapping.map(updatedPlaylist))
        appDatabase.playlistDao().insertTrack(playlistMapping.map(track))
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
        return playlistMapping.map(playlistEntity)
    }

    override fun getTracks(ids: List<Long>): Flow<List<Track>> = flow {
        val tracksList = ids.map { id ->
            appDatabase.playlistDao().getTracks(id)
        }
        emit(tracksList.reversed().map { playlistMapping.map(it) })
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        val playlist = getPlaylistById(playlistId)
        val tracks = playlist.listIdsTracks.filter { it != trackId }
        val newPlaylist = playlist.copy(
            listIdsTracks = tracks,
            countTracks = playlist.countTracks - 1
        )
        addNewPlaylist(newPlaylist)

        val playlists = appDatabase.playlistDao().getAllListPlaylists()
        var isTrackInOtherPlaylist = false
        for (entity in playlists) {
            if (entity.listIdsTracks.contains(trackId.toString())) {
                isTrackInOtherPlaylist = true
                break
            }
        }
        if (!isTrackInOtherPlaylist) {
            appDatabase.playlistDao().deleteTrackById(trackId)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            appDatabase.playlistDao().deletePlaylistById(playlist.id)

            coroutineScope {
                val jobs = playlist.listIdsTracks.map { trackId ->
                    async {
                        isTrackInOtherPlaylists(trackId)
                    }
                }
                jobs.awaitAll()
            }
        }
    }

    private suspend fun isTrackInOtherPlaylists(trackId: Long) {
        appDatabase.playlistDao().getListPlaylists().collect { playlists ->
            var isTrackInOtherPlaylist = false
            for (entity in playlists) {
                if (entity.listIdsTracks.contains(trackId.toString())) {
                    isTrackInOtherPlaylist = true
                    break
                }
            }
            if (!isTrackInOtherPlaylist) {
                appDatabase.playlistDao().deleteTrackById(trackId)
            }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistMapping.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

}