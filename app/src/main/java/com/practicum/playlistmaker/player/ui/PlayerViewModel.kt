package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media_library.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.media_library.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.TrackInfo
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val _trackInfo = MutableLiveData(TrackInfo())
    val trackInfo: LiveData<TrackInfo> get() = _trackInfo

    private val _playerState = MutableLiveData(STATE_DEFAULT)
    val playerState: LiveData<Int> get() = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val stateLiveData = MutableStateFlow<ListPlaylistState>(ListPlaylistState.Empty)
    fun observeState(): StateFlow<ListPlaylistState> = stateLiveData

    private fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getListPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        stateLiveData.value = ListPlaylistState.Empty
                    } else {
                        stateLiveData.value = ListPlaylistState.Content(playlists)
                    }
                }
        }
    }

    init {
        getPlaylists()
    }

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            if (playlist.listIdsTracks.contains(track.trackId)) {
                stateLiveData.value = ListPlaylistState.Duplicate(playlist)
                getPlaylists()
            } else {
                val updatedPlaylist = playlist.copy(
                    listIdsTracks = playlist.listIdsTracks + listOf(track.trackId),
                    countTracks = playlist.listIdsTracks.size + 1
                )
                playlistInteractor.addTrackToPlaylist(updatedPlaylist, track)
                stateLiveData.value = ListPlaylistState.SuccessAdd(playlist)
            }
        }
    }

    fun checkTrackIsFavorite(trackId: Long) {
        viewModelScope.launch {
            val isFavorite = trackId in favoriteTrackInteractor.getFavoriteIdList()
            _isFavorite.postValue(isFavorite)
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoriteTrackInteractor.removeTrackFromFavorites(track)
                _isFavorite.postValue(false)
            } else {
                favoriteTrackInteractor.addTrackToFavorites(track.apply { this.isFavorite= true })
                _isFavorite.postValue(true)
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
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (audioPlayerInteractor.isPlaying()) {
                delay(DELAY)
                val time = DateTimeUtil.simpleFormatTrack(audioPlayerInteractor.getPlaybackPosition().toLong())
                _trackInfo.postValue(_trackInfo.value?.copy(currentPosition = time))
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
        _trackInfo.postValue(_trackInfo.value?.copy(currentPosition = "00:00"))
    }

    private fun stopProgressUpdates() {
        timerJob?.cancel()
    }

    sealed interface ListPlaylistState {
        data object Empty : ListPlaylistState
        data class Duplicate(val playlists: Playlist) : ListPlaylistState
        data class SuccessAdd(val playlists: Playlist) : ListPlaylistState
        data class Content(val playlists: List<Playlist>) : ListPlaylistState
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