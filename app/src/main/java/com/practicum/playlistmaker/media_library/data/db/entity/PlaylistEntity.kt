package com.practicum.playlistmaker.media_library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val listIdsTracks: String = Gson().toJson(emptyList<Long>()),
    val countTracks: Int
)