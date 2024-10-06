package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getTheme(): Theme
    fun selectTheme(select: Theme)
}