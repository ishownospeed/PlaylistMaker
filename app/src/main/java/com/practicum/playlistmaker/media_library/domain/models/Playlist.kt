package com.practicum.playlistmaker.media_library.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val imagePath: String? = null,
    val listIdsTracks: List<Long> = listOf(),
    val countTracks: Int = 0
) : Parcelable
