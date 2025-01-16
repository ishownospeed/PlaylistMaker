package com.practicum.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.media_library.ui.PlaylistViewModel.ListPlaylistState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>() {

    private val viewModel by viewModel<PlaylistViewModel>()

    private var adapter: PlaylistAdapter? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter()
        binding.playlists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlists.adapter = adapter

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeState().collect {
                render(it)
            }
        }

    }

    private fun render(state: ListPlaylistState) {
        when (state) {
            is ListPlaylistState.Empty -> showEmpty()
            is ListPlaylistState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        binding.placeholderNonePlaylist.isVisible = true
        binding.playlists.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        adapter?.updateTracks(playlists)
        binding.playlists.isVisible = true
        binding.placeholderNonePlaylist.isVisible = false
    }

    companion object {
        fun newInstance(): PlaylistFragment = PlaylistFragment()
    }

}