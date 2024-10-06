package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.Theme
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _theme = MutableLiveData<Theme>()
    val theme: LiveData<Theme> = _theme

    init {
        _theme.postValue(settingsInteractor.getTheme())
    }

    fun switchTheme(isDark: Boolean) {
        val newSettings = Theme(isDark)
        settingsInteractor.selectTheme(newSettings)
        _theme.postValue(newSettings)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTermsUse() {
        sharingInteractor.openTermsUse()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SettingsViewModel(
                        settingsInteractor = Creator.provideSettingsInteractor(),
                        sharingInteractor = Creator.provideSharingInteractor()
                    )
                }
            }
        }
    }

}