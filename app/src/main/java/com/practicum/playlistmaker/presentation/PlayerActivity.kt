package com.practicum.playlistmaker.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding

    private val handler: Handler = Handler(Looper.getMainLooper())

    private var mediaPlayer = MediaPlayer()

    private val currentPosition = object : Runnable {
        override fun run() {
            binding.progressTime.text =
                DateTimeUtil.simpleFormatTrack(mediaPlayer.currentPosition.toLong())
            handler.postDelayed(this, DELAY)
        }
    }

    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track: Track? = intent.getParcelableExtra(SearchActivity.TRACK)

        preparePlayer(track)

        binding.buttonPlay.setOnClickListener {
            playbackControl()
        }

        binding.backArrowButton.setOnClickListener { finish() }

        if (track != null) {
            with(binding) {
                Glide.with(applicationContext)
                    .load(track.getCoverArtwork())
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.icon_padding_8dp)))
                    .into(imageAlbum)

                trackNameAudioPlayer.text = track.trackName
                artistAudioPlayer.text = track.artistName
                trackTime.text = track.trackTimeMillis
                albumName.text = track.collectionName.ifEmpty { "" }
                yearName.text = track.releaseDate.substring(0, 4)
                genreName.text = track.primaryGenreName
                countryName.text = track.country
                progressTime.text = track.trackTimeMillis
            }
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        handler.removeCallbacks(currentPosition)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    private fun preparePlayer(track: Track?) {
        if (track != null) {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
                handler.removeCallbacksAndMessages(null)
                binding.progressTime.text = getString(R.string.null_time)
                binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        handler.post(currentPosition)
        binding.buttonPlay.setImageResource(R.drawable.ic_button_pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(currentPosition)
        handler.post(currentPosition)
        binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val DELAY = 400L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

}