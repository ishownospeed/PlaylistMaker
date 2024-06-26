package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowButton.setOnClickListener { finish() }

        val track: Track? = intent.getParcelableExtra(SearchActivity.TRACK, Track::class.java)

        if (track != null) {
            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.icon_padding_8dp)))
                .into(binding.imageAlbum)

            binding.trackNameAudioPlayer.text = track.trackName
            binding.artistAudioPlayer.text = track.artistName
            binding.trackTime.text = DateTimeUtil.simpleFormatTrack(track.trackTimeMillis)
            binding.albumName.text = track.collectionName.ifEmpty { "" }
            binding.yearName.text = track.releaseDate.substring(0, 4)
            binding.genreName.text = track.primaryGenreName
            binding.countryName.text = track.country
        }

    }

}