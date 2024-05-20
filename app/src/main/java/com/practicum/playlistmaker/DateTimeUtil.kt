package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {

    fun simpleFormatTrack(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

}