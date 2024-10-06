package com.practicum.playlistmaker.player.domain.api

interface AudioPlayerRepository {
    fun prepare(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun reset()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}