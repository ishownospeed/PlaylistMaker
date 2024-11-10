package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMainBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.media_library.ui.MediaLibraryFragment
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.settings.ui.SettingsFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSearch.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                add(R.id.fragment_container, SearchFragment())
                addToBackStack(null)
                setReorderingAllowed(true)
            }
        }

        binding.buttonMediaLibrary.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                add(R.id.fragment_container, MediaLibraryFragment())
                addToBackStack(null)
                setReorderingAllowed(true)
            }
        }

        binding.buttonSettings.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                add(R.id.fragment_container, SettingsFragment())
                addToBackStack(null)
                setReorderingAllowed(true)
            }
        }
    }

}