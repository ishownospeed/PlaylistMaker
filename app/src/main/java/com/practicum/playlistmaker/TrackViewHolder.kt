package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
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
            .fitCenter()
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(dpToPx(2.0f, itemView.context)))
            .into(image)

        track.text = item.trackName
        artist.text = item.artistName
        time.text = item.trackTime
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}