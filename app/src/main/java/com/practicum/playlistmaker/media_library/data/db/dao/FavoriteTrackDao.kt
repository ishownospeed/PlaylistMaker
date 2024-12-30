package com.practicum.playlistmaker.media_library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media_library.data.db.entity.TrackEntity

@Dao
interface FavoriteTrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorites_tracks_table ORDER BY sortTimes DESC")
    suspend fun getFavoriteList(): List<TrackEntity>

    @Query("SELECT trackId FROM favorites_tracks_table")
    suspend fun getFavoriteIdList(): List<Int>

}