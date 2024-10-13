package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.Theme

class SettingsRepositoryImpl(private val storage: SettingsThemeStorage) : SettingsRepository {

    override fun getTheme(): Theme {
        val theme = storage.getThemeSettings().darkTheme
        return Theme(theme)
    }

    override fun selectTheme(select: Theme) {
        storage.saveThemeSettings(select)
    }
}