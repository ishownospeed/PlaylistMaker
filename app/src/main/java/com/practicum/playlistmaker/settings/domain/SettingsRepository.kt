package com.practicum.playlistmaker.settings.domain

interface SettingsRepository {
    fun getTheme(): Theme
    fun selectTheme(select: Theme)
}