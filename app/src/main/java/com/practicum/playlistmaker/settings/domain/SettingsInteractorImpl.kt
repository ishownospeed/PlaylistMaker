package com.practicum.playlistmaker.settings.domain

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun getTheme(): Theme {
        return repository.getTheme()
    }

    override fun selectTheme(select: Theme) {
        repository.selectTheme(select)
    }
}