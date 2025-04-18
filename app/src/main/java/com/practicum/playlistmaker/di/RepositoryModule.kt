package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media_library.data.FavoriteTrackRepositoryImpl
import com.practicum.playlistmaker.media_library.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media_library.data.mapping.PlaylistMapping
import com.practicum.playlistmaker.media_library.data.mapping.TrackMapping
import com.practicum.playlistmaker.media_library.data.repository.SaveImageStorageRepositoryImpl
import com.practicum.playlistmaker.media_library.domain.api.FavoriteTrackRepository
import com.practicum.playlistmaker.media_library.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media_library.domain.api.SaveImageStorageRepository
import com.practicum.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }

    factory { TrackMapping() }

    factory { PlaylistMapping(get()) }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    factory<SaveImageStorageRepository> {
        SaveImageStorageRepositoryImpl(get())
    }

}