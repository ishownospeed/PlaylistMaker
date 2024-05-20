package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.trackImage)
    private val track: TextView = itemView.findViewById(R.id.trackName)
    private val artist: TextView = itemView.findViewById(R.id.artistName)
    private val time: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(item: Track) {
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .fitCenter()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.small_corner_radius)))
            .into(image)

        track.text = item.trackName
        artist.text = item.artistName
        time.text = DateTimeUtil.simpleFormatTrack(item.trackTimeMillis)
    }

}