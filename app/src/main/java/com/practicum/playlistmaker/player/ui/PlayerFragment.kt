package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.player.ui.PlayerViewModel.ListPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    private val viewModel by viewModel<PlayerViewModel>()

    private var playlistAdapter: BottomSheetPlayerAdapter? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    companion object {
        private const val TRACK = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK to track)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.backArrowButton.setOnClickListener { findNavController().navigateUp() }

        val track: Track? = requireArguments().getParcelable(TRACK)

        if (track != null) {
            initTrack(track)
            track.previewUrl?.let { viewModel.preparePlayer(it) }
        }

        playlistAdapter = BottomSheetPlayerAdapter { playlist ->
            track?.let { viewModel.addTrackToPlaylist(playlist, it) }
        }
        binding.bottomSheetPlaylists.adapter = playlistAdapter

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

        viewModel.trackInfo.observe(viewLifecycleOwner) {
            binding.progressTime.text = it?.currentPosition
        }

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerViewModel.STATE_PLAYING -> binding.buttonPlay.setImageResource(R.drawable.ic_button_pause)
                PlayerViewModel.STATE_PAUSED, PlayerViewModel.STATE_PREPARED -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                }

                PlayerViewModel.STATE_COMPLETED -> {
                    binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
                }
            }
        }

        binding.buttonFavorite.setOnClickListener {
            track?.let {
                viewModel.onFavoriteClicked(it)
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner){ isFavorite ->
            updateLikeButton(isFavorite)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeState().collect {
                render(it)
            }
        }

        binding.buttonAddPlaylist.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (viewModel.playerState.value == PlayerViewModel.STATE_PLAYING) viewModel.pausePlayer()
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        if (viewModel.playerState.value == PlayerViewModel.STATE_PAUSED) viewModel.startPlayer()
                    }
                    else -> binding.overlay.isVisible = true
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

    }

    private fun render(state: ListPlaylistState) {
        when (state) {
            is ListPlaylistState.Empty -> showEmpty()
            is ListPlaylistState.Duplicate -> {
                Toast.makeText(
                    context,
                    "Трек уже добавлен в плейлист ${state.playlists.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is ListPlaylistState.SuccessAdd -> {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    context,
                    "Добавлено в плейлист ${state.playlists.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is ListPlaylistState.Content -> content(state.playlists)
        }
    }

    private fun showEmpty() = with(binding) {
        placeholderNonePlaylist.isVisible = true
        bottomSheetPlaylists.isVisible = false
    }

    private fun content(playlists: List<Playlist>) {
        binding.bottomSheetPlaylists.isVisible = true
        binding.placeholderNonePlaylist.isVisible = false
        playlistAdapter?.submitList(playlists)
    }

    override fun onStop() {
        super.onStop()
        viewModel.pausePlayer()
    }

    private fun initTrack(track: Track) {
        Glide.with(requireActivity())
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(requireActivity().resources.getDimensionPixelSize(R.dimen.icon_padding_8dp)))
            .into(binding.imageAlbum)

        binding.trackNameAudioPlayer.text = track.trackName
        binding.artistAudioPlayer.text = track.artistName
        binding.trackTime.text = track.trackTimeMillis
        binding.albumName.text = track.collectionName?.ifEmpty { "" } ?: ""
        binding.yearName.text = track.releaseDate.substring(0, 4)
        binding.genreName.text = track.primaryGenreName
        binding.countryName.text = track.country
        binding.progressTime.text = track.trackTimeMillis
        viewModel.checkTrackIsFavorite(track.trackId)
    }

    private fun playbackControl() {
        when (viewModel.playerState.value) {
            PlayerViewModel.STATE_PLAYING -> viewModel.pausePlayer()
            PlayerViewModel.STATE_PREPARED, PlayerViewModel.STATE_PAUSED -> viewModel.startPlayer()
            PlayerViewModel.STATE_COMPLETED -> viewModel.startPlayer()
        }
    }

    private fun updateLikeButton(isFavorite: Boolean) {
        val favorite = if (isFavorite) R.drawable.ic_button_favorite else R.drawable.ic_button_not_favorite
        binding.buttonFavorite.setImageResource(favorite)
    }

}