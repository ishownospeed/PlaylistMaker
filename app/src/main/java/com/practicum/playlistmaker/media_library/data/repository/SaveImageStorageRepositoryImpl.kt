package com.practicum.playlistmaker.media_library.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.media_library.domain.api.SaveImageStorageRepository
import java.io.File
import java.io.FileOutputStream

class SaveImageStorageRepositoryImpl(private val context: Context) : SaveImageStorageRepository {

    override fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, uri.toString().substringAfterLast("/"))
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri()
    }

}