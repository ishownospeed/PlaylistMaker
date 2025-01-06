package com.practicum.playlistmaker.media_library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : BaseFragment<FragmentFavoriteTracksBinding>() {

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    private lateinit var adapter: TrackAdapter
    private var isClickAllowed = true
    private var debounceJob: Job? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter { track ->
            movePlayerFragment(track)
        }
        binding.favoriteList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeState().collect {
                render(it)
            }
        }

    }

    private fun movePlayerFragment(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            debounceJob?.cancel()
            debounceJob = CoroutineScope(Dispatchers.Main).launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Empty -> showEmpty()
            is FavoriteState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        binding.favoriteList.isVisible = false
        binding.progressBar.isVisible = false
        binding.placeholderEmptyFavoriteTracks.isVisible = true
    }


    private fun showContent(tracks: List<Track>) {
        adapter.updateTracks(tracks)
        binding.favoriteList.isVisible = true
        binding.placeholderEmptyFavoriteTracks.isVisible = false
        binding.progressBar.isVisible = false
    }

    companion object {
        fun newInstance(): FavoriteTracksFragment = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}