package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil

class PlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val _track = MutableLiveData(Track())
    val track: LiveData<Track> get() = _track

    private val _playerState = MutableLiveData(STATE_DEFAULT)
    val playerState: LiveData<Int> get() = _playerState

    private val currentPosition = object : Runnable {
        override fun run() {
            if (audioPlayerInteractor.isPlaying()) {
                val time = DateTimeUtil.simpleFormatTrack(audioPlayerInteractor.getPlaybackPosition().toLong())
                _track.postValue(_track.value?.copy(currentPosition = time))
                handler.postDelayed(this, DELAY)
            }
        }
    }

    fun preparePlayer(trackUrl: String) {
        if (playerState.value == STATE_DEFAULT) {
            audioPlayerInteractor.prepareTrack(
                trackUrl,
                onPrepared = {
                    _playerState.value = STATE_PREPARED
                },
                onCompletion = {
                    _playerState.value = STATE_COMPLETED
                    resetTrackTime()
                    stopProgressUpdates()
                }
            )
        }
    }

    fun startPlayer() {
        audioPlayerInteractor.startPlayback()
        _playerState.postValue(STATE_PLAYING)
        handler.post(currentPosition)
    }

    fun pausePlayer() {
        if (_playerState.value != STATE_PREPARED) {
            audioPlayerInteractor.pausePlayback()
            _playerState.postValue(STATE_PAUSED)
            stopProgressUpdates()
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun releasePlayer() {
        audioPlayerInteractor.releasePlayer()
        stopProgressUpdates()
    }

    private fun resetTrackTime() {
        _track.postValue(_track.value?.copy(currentPosition = "00:00"))
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(currentPosition)
    }

    companion object {
        private const val DELAY = 400L
        private const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETED = 4
    }

}