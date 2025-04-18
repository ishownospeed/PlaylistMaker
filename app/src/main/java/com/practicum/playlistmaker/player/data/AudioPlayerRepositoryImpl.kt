package com.practicum.playlistmaker.player.data

import com.practicum.playlistmaker.player.domain.api.AudioPlayerRepository

class AudioPlayerRepositoryImpl(private val audioPlayer: AudioPlayer) : AudioPlayerRepository {

    override fun prepare(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        audioPlayer.prepare(trackUrl, onPrepared, onCompletion)
    }

    override fun start() {
        audioPlayer.start()
    }

    override fun pause() {
        audioPlayer.pause()
    }

    override fun reset() {
        audioPlayer.reset()
    }

    override fun release() {
        audioPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return audioPlayer.isPlaying()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.getCurrentPosition()
    }
}