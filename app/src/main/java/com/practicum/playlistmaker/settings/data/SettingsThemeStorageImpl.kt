package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.Theme

class SettingsThemeStorageImpl(private val sharedPreferences: SharedPreferences) :
    SettingsThemeStorage {

    override fun getThemeSettings(): Theme {
        val theme = sharedPreferences.getBoolean(THEME_KEY, false)
        return Theme(theme)
    }

    override fun saveThemeSettings(select: Theme) {
        AppCompatDelegate.setDefaultNightMode(
            if (select.darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit().putBoolean(THEME_KEY, select.darkTheme).apply()
    }

    private companion object {
        const val THEME_KEY = "key_for_theme"
    }
}