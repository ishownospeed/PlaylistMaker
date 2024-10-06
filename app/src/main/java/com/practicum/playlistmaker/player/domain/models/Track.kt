package com.practicum.playlistmaker.player.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int = 0,
    val trackName: String = "",
    val artistName: String = "",
    val trackTimeMillis: String = "",
    val artworkUrl100: String? = "",
    val collectionName: String = "",
    val releaseDate: String = "",
    val primaryGenreName: String = "",
    val country: String = "",
    val previewUrl: String = "",
    val currentPosition: String = "00:00"
) : Parcelable