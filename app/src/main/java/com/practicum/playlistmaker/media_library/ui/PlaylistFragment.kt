package com.practicum.playlistmaker.media_library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>() {

    private val viewModel by viewModel<PlaylistViewModel>()

    companion object {

        fun newInstance(): PlaylistFragment = PlaylistFragment()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

}