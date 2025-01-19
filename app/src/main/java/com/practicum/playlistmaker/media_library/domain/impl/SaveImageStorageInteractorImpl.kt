package com.practicum.playlistmaker.media_library.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.media_library.domain.api.SaveImageStorageInteractor
import com.practicum.playlistmaker.media_library.domain.api.SaveImageStorageRepository

class SaveImageStorageInteractorImpl(
    private val repository: SaveImageStorageRepository
) : SaveImageStorageInteractor {

    override fun saveImageToPrivateStorage(uri: Uri): Uri {
        return repository.saveImageToPrivateStorage(uri)
    }

}