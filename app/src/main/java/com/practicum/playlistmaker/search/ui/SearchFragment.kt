package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.main.ui.base.BaseFragment
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private var inputValue: String = ""

    private var isClickAllowed = true
    private var debounceJob: Job? = null

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            clearListOnClick()
            hideKeyboard()
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel.repeatRequest()
            hideKeyboard()
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
            historyAdapter.clearTracks()
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.changeInputEditTextState(hasFocus, binding.inputEditText.text.toString())
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputEditText.text.toString())
            }
            false
        }

        binding.inputEditText.addTextChangedListener(
            onTextChanged = { charSequence, _, _, _ ->
                viewModel.changeInputEditTextState(
                    binding.inputEditText.hasFocus(),
                    charSequence.toString()
                )
                inputValue = charSequence.toString()
            }
        )

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.SearchList -> showTracks(state.tracks)
                is SearchState.HistoryList -> showHistory(state.tracks)
                is SearchState.Error -> showPlaceholderError()
                is SearchState.Empty -> showPlaceholderNothingFound()
            }
        }

        searchAdapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(item: Track) {
                viewModel.saveTrackInHistory(item)
                movePlayerFragment(item)
            }
            override fun onItemLongClick(item: Track): Boolean {
                return false
            }
        })
        binding.listTracks.adapter = searchAdapter

        historyAdapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(item: Track) {
                viewModel.saveTrackInHistory(item)
                movePlayerFragment(item)
            }
            override fun onItemLongClick(item: Track): Boolean {
                return false
            }
        })
        binding.listTracksHistory.adapter = historyAdapter

        viewModel.hideKeyboardEvent.observe(viewLifecycleOwner) {
            hideKeyboard()
        }

        viewModel.isClearIconVisibile.observe(viewLifecycleOwner) {
            binding.clearIcon.isVisible = it
        }
    }

    private fun clearListOnClick() {
        searchAdapter.clearTracks()
        binding.containerHistory.isVisible = historyAdapter.itemCount > 0
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderUploadFailed.isVisible = false
        binding.buttonUpdate.isVisible = false
    }

    private fun showLoading() {
        binding.progressStatus.isVisible = true
        binding.listTracks.isVisible = false
        binding.containerHistory.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderUploadFailed.isVisible = false
        binding.buttonUpdate.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        searchAdapter.updateTracks(tracks)
        binding.listTracks.isVisible = true
        binding.progressStatus.isVisible = false
        binding.containerHistory.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderUploadFailed.isVisible = false
        binding.buttonUpdate.isVisible = false
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateTracks(tracks)
        binding.containerHistory.isVisible = tracks.isNotEmpty()
        binding.listTracks.isVisible = false
        binding.progressStatus.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderUploadFailed.isVisible = false
        binding.buttonUpdate.isVisible = false
    }

    private fun showPlaceholderError() {
        binding.placeholderUploadFailed.isVisible = true
        binding.buttonUpdate.isVisible = true
        binding.placeholderNothingFound.isVisible = false
        binding.containerHistory.isVisible = false
        binding.listTracks.isVisible = false
        binding.progressStatus.isVisible = false
    }

    private fun showPlaceholderNothingFound() {
        binding.placeholderNothingFound.isVisible = true
        binding.placeholderUploadFailed.isVisible = false
        binding.buttonUpdate.isVisible = false
        binding.progressStatus.isVisible = false
        binding.containerHistory.isVisible = false
        binding.listTracks.isVisible = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_STATE, inputValue)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        inputValue = savedInstanceState?.getString(SAVE_STATE).toString()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun movePlayerFragment(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            debounceJob?.cancel()
            debounceJob = CoroutineScope(Dispatchers.Main).launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val SAVE_STATE = "save_state"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}