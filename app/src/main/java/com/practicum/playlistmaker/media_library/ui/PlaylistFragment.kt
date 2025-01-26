package com.practicum.playlistmaker.media_library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.DateTimeUtil
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>() {

    private val viewModel by viewModel<PlaylistViewModel>()

    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var playlist: Playlist? = null
    private var adapter: TrackAdapter? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getParcelable(PLAYLIST)
        playlist?.let { viewModel.loadPlaylistById(it.id) }

        binding.toolbar.setOnClickListener { findNavController().navigateUp() }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> binding.overlay.isVisible = true
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.sharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.menuPlaylist.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.playlistShareMenu.setOnClickListener {
            sharePlaylist()
        }

        binding.playlistDeleteMenu.setOnClickListener {
            isDeletePlaylist()
        }

        binding.playlistEditInformationMenu.setOnClickListener {
            val playlist = viewModel.playlist().value
            findNavController().navigate(
                R.id.action_playlistFragment_to_newPlaylistFragment,
                bundleOf(PLAYLIST to playlist)
            )
        }

        adapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(item: Track) {
                movePlayerFragment(item)
            }
            override fun onItemLongClick(item: Track): Boolean {
                isDeleteTrack(item)
                return true
            }
        })
        binding.bottomSheetTracks.adapter = adapter

        viewModel.playlist().observe(viewLifecycleOwner) {
            showContentPlaylist(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tracks().collect { state ->
                when (state) {
                    is PlaylistViewModel.TracksState.Content -> {
                        binding.bottomSheetTracks.isVisible = true
                        binding.progressStatus.isVisible = false
                        binding.placeholderNonePlaylist.isVisible = false
                        showContentTracks(state.tracks)
                    }
                    PlaylistViewModel.TracksState.Empty -> {
                        showContentTracks(emptyList())
                        binding.placeholderNonePlaylist.isVisible = true
                        binding.progressStatus.isVisible = false
                        binding.bottomSheetTracks.isVisible = false
                    }
                    PlaylistViewModel.TracksState.Loading -> {
                        binding.progressStatus.isVisible = true
                        binding.placeholderNonePlaylist.isVisible = false
                        binding.bottomSheetTracks.isVisible = false
                    }
                }
            }
        }

    }

    private fun showContentPlaylist(playlist: Playlist) {
        if (playlist.imagePath?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(playlist.imagePath)
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .into(binding.imageAlbum)
            Glide.with(requireContext())
                .load(playlist.imagePath)
                .placeholder(R.drawable.ic_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.small_corner_radius))
                )
                .into(binding.imagePlaylistMenu)
        }
        binding.namePlaylist.text = playlist.name
        binding.namePlaylistMenu.text = playlist.name
        if (!playlist.description.isNullOrEmpty()) {
            binding.contentPlaylist.isVisible = true
            binding.contentPlaylist.text = playlist.description
        } else {
            binding.contentPlaylist.isVisible = false
        }
    }

    private fun showContentTracks(tracks: List<Track>) {
        adapter?.updateTracks(tracks)
        binding.minutes.text = durationTracks(tracks)
        val count = String.format(Locale.getDefault(), "%d треков", tracks.size)
        binding.countTracks.text = count
        binding.countTracksMenu.text = count
    }

    private fun durationTracks(tracks: List<Track>): String {
        val durationSum = tracks.sumOf { it.trackTimeMillis }
        return DateTimeUtil.durationSum(durationSum) + " минут"
    }

    private fun movePlayerFragment(track: Track) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }

    private fun isDeleteTrack(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Хотите удалить трек?")
            .setPositiveButton("ДА") { dialog, _ ->
                playlist?.let { viewModel.removeTrackFromPlaylist(track.trackId, it.id) }
                dialog.dismiss()
            }
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun isDeletePlaylist() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист \"${playlist?.name}\"?")
            .setPositiveButton("ДА") { dialog, _ ->
                playlist?.let { viewModel.removePlaylist(it) }
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sharePlaylist() {
        val tracks = viewModel.tracks().value

        if ((tracks is PlaylistViewModel.TracksState.Content) && !playlist?.listIdsTracks.isNullOrEmpty() && playlist != null) {
            val message = buildString {
                appendLine(playlist?.name)
                playlist?.description?.let { appendLine(it) }
                appendLine("${tracks.tracks.size} треков")

                tracks.tracks.forEachIndexed { index, track ->
                    val trackTime = DateTimeUtil.simpleFormatTrack(track.trackTimeMillis)
                    appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($trackTime)")
                }
            }

            val shareIntent = Intent().apply {
                putExtra(Intent.EXTRA_TEXT, message)
                action = Intent.ACTION_SEND
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
        } else {
            Toast.makeText(
                requireContext(),
                "В этом плейлисте нет списка треков, которым можно поделиться",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val PLAYLIST = "playlist"

        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(PLAYLIST to playlist)
    }

}