package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        sharedPrefs = Creator.provideSharedPreferences()
        if (sharedPrefs.contains(THEME_KEY)) switchTheme() else savePrimaryTheme()
    }

    private fun switchTheme() {
        val darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun savePrimaryTheme() {
        val darkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        saveStateTheme(darkTheme)
    }

    private fun saveStateTheme(checked: Boolean) {
        sharedPrefs.edit().putBoolean(THEME_KEY, checked).apply()
    }

    private companion object {
        const val THEME_KEY = "key_for_theme"
    }
}