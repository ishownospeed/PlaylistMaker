package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowButton.setOnClickListener { finish() }

        val track: Track? = intent.getParcelableExtra(SearchActivity.TRACK)

        if (track != null) {
            initTrack(track)
            viewModel.preparePlayer(track.previewUrl)
        }

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

        viewModel.track.observe(this) {
            binding.progressTime.text =
                if (it?.currentPosition.equals("00:00")) track?.trackTimeMillis
                else it?.currentPosition
        }

        viewModel.playerState.observe(this) { state ->
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
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.icon_padding_8dp)))
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