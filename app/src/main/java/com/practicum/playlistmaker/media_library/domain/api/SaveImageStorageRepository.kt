package com.practicum.playlistmaker.media_library.domain.api

import android.net.Uri

interface SaveImageStorageRepository {
    fun saveImageToPrivateStorage(uri: Uri): Uri
}