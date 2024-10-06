package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer

class AudioPlayerImpl(private val mediaPlayer: MediaPlayer) : AudioPlayer {

    override fun prepare(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrepared.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion.invoke()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}