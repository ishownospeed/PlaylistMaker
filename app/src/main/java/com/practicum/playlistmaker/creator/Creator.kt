package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.AudioPlayerImpl
import com.practicum.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.data.SettingsThemeStorage
import com.practicum.playlistmaker.settings.data.SettingsThemeStorageImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.data.SharingProviderImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

object Creator {

    private const val THEME_PREFERENCES = "theme_preferences"
    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context = application)
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(provideSettingsStorage())
    }

    private fun provideSettingsStorage(): SettingsThemeStorage {
        return SettingsThemeStorageImpl(provideSharedPreferences())
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingProviderImpl(application)
    }

    private fun provideAudioPlayer(): AudioPlayerImpl {
        return AudioPlayerImpl(MediaPlayer())
    }

    private fun provideAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(provideAudioPlayer())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(provideAudioPlayerRepository())
    }

}