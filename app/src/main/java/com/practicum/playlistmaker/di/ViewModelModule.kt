package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media_library.ui.FavoriteTracksViewModel
import com.practicum.playlistmaker.media_library.ui.ListPlaylistViewModel
import com.practicum.playlistmaker.media_library.ui.NewPlaylistViewModel
import com.practicum.playlistmaker.media_library.ui.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        ListPlaylistViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

}