package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val audioPlayerInteractor: AudioPlayerInteractor) : ViewModel() {

    private var timerJob: Job? = null

    private val _track = MutableLiveData(Track())
    val track: LiveData<Track> get() = _track

    private val _playerState = MutableLiveData(STATE_DEFAULT)
    val playerState: LiveData<Int> get() = _playerState

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
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (audioPlayerInteractor.isPlaying()) {
                delay(DELAY)
                val time = DateTimeUtil.simpleFormatTrack(audioPlayerInteractor.getPlaybackPosition().toLong())
                _track.postValue(_track.value?.copy(currentPosition = time))
            }
        }
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
        timerJob?.cancel()
    }

    companion object {
        private const val DELAY = 300L
        private const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETED = 4
    }

}