package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {

    private val viewModel by viewModel<PlayerViewModel>()

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

        binding.backArrowButton.setOnClickListener { findNavController().navigateUp() }

        val track: Track? = requireArguments().getParcelable(TRACK)

        if (track != null) {
            initTrack(track)
            viewModel.preparePlayer(track.previewUrl)
        }

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

        viewModel.track.observe(viewLifecycleOwner) {
            binding.progressTime.text =
                if (it?.currentPosition.equals("00:00")) track?.trackTimeMillis
                else it?.currentPosition
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
        binding.albumName.text = track.collectionName.ifEmpty { "" }
        binding.yearName.text = track.releaseDate.substring(0, 4)
        binding.genreName.text = track.primaryGenreName
        binding.countryName.text = track.country
        binding.progressTime.text = track.trackTimeMillis
    }

    private fun playbackControl() {
        when (viewModel.playerState.value) {
            PlayerViewModel.STATE_PLAYING -> viewModel.pausePlayer()
            PlayerViewModel.STATE_PREPARED, PlayerViewModel.STATE_PAUSED -> viewModel.startPlayer()
            PlayerViewModel.STATE_COMPLETED -> viewModel.startPlayer()
        }
    }

}