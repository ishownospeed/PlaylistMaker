package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackViewBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var tracks: MutableList<Track> = mutableListOf()

    fun updateTracks(newTracks: List<Track>) {
        val oldTracks = tracks
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldTracks.size
            }

            override fun getNewListSize(): Int {
                return newTracks.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldTracks[oldItemPosition].trackId == newTracks[newItemPosition].trackId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldTracks[oldItemPosition] == newTracks[newItemPosition]
            }

        })
        tracks = newTracks.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    fun clearTracks() {
        tracks.clear()
        this.notifyDataSetChanged()
    }

    fun interface OnItemClickListener {
        fun onItemClick(item: Track)
    }

    inner class TrackViewHolder(private val binding: TrackViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Track) {
            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder)
                .fitCenter()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.small_corner_radius)))
                .into(binding.trackImage)

            binding.trackName.text = item.trackName
            binding.artistName.text = item.artistName
            binding.trackTime.text = item.trackTimeMillis

            binding.root.setOnClickListener {
                onItemClickListener.onItemClick(item)
            }
        }
    }

}