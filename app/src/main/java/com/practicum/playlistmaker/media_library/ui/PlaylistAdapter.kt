package com.practicum.playlistmaker.media_library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import java.util.Locale

class PlaylistAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private var playlists: MutableList<Playlist> = mutableListOf()

    fun updateTracks(newPlaylists: List<Playlist>) {
        val oldPlaylists = playlists
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldPlaylists.size
            }

            override fun getNewListSize(): Int {
                return newPlaylists.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldPlaylists[oldItemPosition].id == newPlaylists[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldPlaylists[oldItemPosition] == newPlaylists[newItemPosition]
            }

        })
        playlists = newPlaylists.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(private val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Playlist) {
            Glide.with(itemView)
                .load(item.imagePath)
                .transform(
                    CenterCrop(),
                    RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.icon_padding_8dp))
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

}