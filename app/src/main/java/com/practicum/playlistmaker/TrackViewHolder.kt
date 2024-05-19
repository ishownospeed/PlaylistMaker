package com.practicum.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.trackImage)
    private val track: TextView = itemView.findViewById(R.id.trackName)
    private val artist: TextView = itemView.findViewById(R.id.artistName)
    private val time: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(item: Track) {
        val cornerRadiusImage = 2.0f

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(cornerRadiusImage, itemView.context)))
            .into(image)

        track.text = item.trackName
        artist.text = item.artistName
        time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}