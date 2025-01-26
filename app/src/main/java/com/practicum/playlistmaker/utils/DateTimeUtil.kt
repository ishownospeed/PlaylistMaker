package com.practicum.playlistmaker.utils

import android.icu.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {

    fun simpleFormatTrack(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

    fun durationSum(durationSum: Long): String {
        return SimpleDateFormat("mm", Locale.getDefault()).format(durationSum)
    }

}