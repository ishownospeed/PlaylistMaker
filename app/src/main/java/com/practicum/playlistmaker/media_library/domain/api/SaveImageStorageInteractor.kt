package com.practicum.playlistmaker.media_library.domain.api

import android.net.Uri

interface SaveImageStorageInteractor {
    fun saveImageToPrivateStorage(uri: Uri): Uri
}