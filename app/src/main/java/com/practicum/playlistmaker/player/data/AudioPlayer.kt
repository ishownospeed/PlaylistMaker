package com.practicum.playlistmaker.player.data

interface AudioPlayer {
    fun prepare(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun reset()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}