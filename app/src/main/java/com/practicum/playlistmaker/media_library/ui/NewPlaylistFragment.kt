package com.practicum.playlistmaker.media_library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : BaseFragment<FragmentNewPlaylistBinding>() {

    private val viewModel by viewModel<NewPlaylistViewModel>()

    private var imageUri: Uri? = null

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
        binding.buttonCreate.isEnabled = false

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
            viewModel.addNewPlaylist(
                Playlist(
                    name = binding.textInputEditTextName.text.toString(),
                    description = binding.textInputEditTextDescription.text.toString(),
                    imagePath = imageUri.toString()
                )
            )
            Toast.makeText(requireContext(),"Плейлист ${binding.textInputEditTextName.text} создан", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun checkFields() {
        if (imageUri != null
            || (binding.textInputEditTextName.text.toString().isNotBlank()
                    || binding.textInputEditTextDescription.text.toString().isNotBlank())
        ) showDialog()
        else findNavController().navigateUp()
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
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, uri.toString().substringAfterLast("/"))
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        imageUri = file.toUri()
        setImageIntoView(file.toUri())
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