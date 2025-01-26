package com.practicum.playlistmaker.media_library.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.media_library.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : BaseFragment<FragmentNewPlaylistBinding>() {

    private val viewModel by viewModel<NewPlaylistViewModel>()

    private var imageUri: Uri? = null
    private var playlist: Playlist? = null
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                saveImageToPrivateStorage(uri)
            } else {
                Toast.makeText(requireContext(), "Ничего не выбрано", Toast.LENGTH_SHORT).show()
            }
        }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPlaylistBinding {
        return FragmentNewPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getParcelable(PlaylistFragment.PLAYLIST)
        if (playlist == null) {
            binding.backArrowButton.text = getString(R.string.new_playlist)
            binding.buttonCreate.text = getString(R.string.button_to_create)
            binding.buttonCreate.isEnabled = false
        } else {
            binding.backArrowButton.text = getString(R.string.edit_playlist)
            binding.buttonCreate.text = getString(R.string.save)
            binding.textInputEditTextName.setText(playlist?.name)
            binding.textInputEditTextDescription.setText(playlist?.description)
            binding.buttonCreate.isEnabled = true

            playlist?.imagePath?.let { uri ->
                if (uri != "null") {
                    imageUri = uri.toUri()
                    setImageIntoView(uri.toUri())
                }
            }
        }

        binding.backArrowButton.setOnClickListener {
            checkFields()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            checkFields()
        }

        binding.imageNewPlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.textInputEditTextName.doOnTextChanged { text, _, _, _ ->
            binding.buttonCreate.isEnabled = text?.isNotEmpty() ?: false
        }

        binding.buttonCreate.setOnClickListener {
            if (playlist == null) {
                viewModel.addNewPlaylist(
                    Playlist(
                        name = binding.textInputEditTextName.text.toString(),
                        description = binding.textInputEditTextDescription.text.toString(),
                        imagePath = imageUri.toString()
                    )
                )
                Toast.makeText(
                    requireContext(),
                    "Плейлист ${binding.textInputEditTextName.text} создан",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                playlist?.let {
                    val copyPlaylist = it.copy(
                        name = binding.textInputEditTextName.text.toString(),
                        description = binding.textInputEditTextDescription.text.toString(),
                        imagePath = imageUri.toString(),
                        listIdsTracks = it.listIdsTracks,
                        countTracks = it.countTracks
                    )
                    viewModel.updatePlaylist(copyPlaylist)
                }
            }
            findNavController().navigateUp()
        }
    }

    private fun checkFields() {
        if (playlist == null) {
            if (imageUri != null
                || (binding.textInputEditTextName.text.toString().isNotBlank()
                        || binding.textInputEditTextDescription.text.toString().isNotBlank())
            ) showDialog()
            else findNavController().navigateUp()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Завершить") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        viewModel.saveImage(uri) {
            imageUri = it
            setImageIntoView(it)
        }
    }

    private fun setImageIntoView(uri: Uri) {
        Glide.with(requireContext())
            .load(uri)
            .transform(
                CenterCrop(),
                RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.icon_padding_8dp))
            )
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.imageNewPlaylist)
    }

}