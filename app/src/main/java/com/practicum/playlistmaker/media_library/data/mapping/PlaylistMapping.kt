package com.practicum.playlistmaker.media_library.data.mapping

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media_library.domain.models.Playlist

class PlaylistMapping(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            listIdsTracks = gson.toJson(playlist.listIdsTracks),
            countTracks = playlist.countTracks
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val trackListType = object : TypeToken<List<Long>>() {}.type
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imagePath = entity.imagePath,
            listIdsTracks = gson.fromJson(entity.listIdsTracks, trackListType),
            countTracks = entity.countTracks
        )
    }

}