package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.settings.domain.Theme

interface SettingsThemeStorage {
    fun saveThemeSettings(select: Theme)
    fun getThemeSettings(): Theme
}