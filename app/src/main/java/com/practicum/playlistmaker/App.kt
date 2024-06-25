package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        val theme = sharedPrefs.getBoolean(THEME_KEY, false)

        if (sharedPrefs.contains(THEME_KEY)) switchTheme(theme) else savePrimaryTheme()
    }

    private fun savePrimaryTheme() {
        darkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        saveStateTheme(darkTheme)
    }

    fun saveStateTheme(checked: Boolean) {
        getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit().putBoolean(THEME_KEY, checked)
            .apply()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private companion object {
        const val THEME_PREFERENCES = "theme_preferences"
        const val THEME_KEY = "key_for_theme"
    }
}