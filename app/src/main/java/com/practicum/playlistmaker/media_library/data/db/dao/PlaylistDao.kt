package com.practicum.playlistmaker.media_library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media_library.data.db.entity.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update(entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists")
    fun getListPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity

    @Query("SELECT * FROM tracks_playlist_table WHERE trackId = :trackId")
    suspend fun getTracks(trackId: Long): TrackInPlaylistEntity

    @Query("DELETE FROM tracks_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("SELECT * FROM playlists")
    suspend fun getAllListPlaylists(): List<PlaylistEntity>

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

}