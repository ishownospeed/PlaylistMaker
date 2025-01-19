package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemBottomSheetBinding
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import java.util.Locale

class BottomSheetPlayerAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<Playlist, BottomSheetPlayerAdapter.BottomSheetPlayerViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetPlayerViewHolder {
        val view = PlaylistItemBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BottomSheetPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetPlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BottomSheetPlayerViewHolder(private val binding: PlaylistItemBottomSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Playlist) {
            Glide.with(itemView)
                .load(item.imagePath)
                .transform(
                    CenterCrop(),
                    RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.small_corner_radius))
                )
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.imagePlaylist)

            binding.namePlaylist.text = item.name
            binding.countTracks.text =
                String.format(Locale.getDefault(), item.countTracks.toString() + " треков")

            binding.root.setOnClickListener {
                onItemClickListener.onItemClick(item)
            }
        }
    }

    fun interface OnItemClickListener {
        fun onItemClick(item: Playlist)
    }

    private class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
            oldItem == newItem
    }

}