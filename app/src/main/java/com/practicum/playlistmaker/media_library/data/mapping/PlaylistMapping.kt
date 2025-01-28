package com.practicum.playlistmaker.media_library.data.mapping

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media_library.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

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

    fun map(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
        )
    }

    fun map(playlistTrackEntity: TrackInPlaylistEntity): Track {
        return Track(
            trackId = playlistTrackEntity.trackId,
            trackName = playlistTrackEntity.trackName,
            artistName = playlistTrackEntity.artistName,
            trackTimeMillis = playlistTrackEntity.trackTimeMillis,
            artworkUrl100 = playlistTrackEntity.artworkUrl100,
            collectionName = playlistTrackEntity.collectionName,
            releaseDate = playlistTrackEntity.releaseDate,
            primaryGenreName = playlistTrackEntity.primaryGenreName,
            country = playlistTrackEntity.country,
            previewUrl = playlistTrackEntity.previewUrl,
            isFavorite = playlistTrackEntity.isFavorite
        )
    }

}