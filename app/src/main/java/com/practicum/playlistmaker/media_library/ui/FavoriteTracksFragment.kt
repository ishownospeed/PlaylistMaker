package com.practicum.playlistmaker.media_library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : BaseFragment<FragmentFavoriteTracksBinding>() {

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    companion object {

        fun newInstance(): FavoriteTracksFragment = FavoriteTracksFragment()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

}