package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var inputValue: String = ""

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
    }

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowLeftSearch.setOnClickListener { finish() }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            clearListOnClick()
            hideKeyboard()
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel.repeatRequest()
            hideKeyboard()
        }

        binding.containerHistory.isVisible = viewModel.isNotEmptySearchHistory()
        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
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

        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.SearchList -> showTracks(state.tracks)
                is SearchState.HistoryList -> showHistory(state.tracks)
                is SearchState.Error -> showPlaceholderError()
                is SearchState.Empty -> showPlaceholderNothingFound()
            }
        }

        searchAdapter = TrackAdapter { track ->
            viewModel.saveTrackInHistory(track)
            movePlayerActivity(track)
        }

        historyAdapter = TrackAdapter { track ->
            viewModel.saveTrackInHistory(track)
            movePlayerActivity(track)
        }

        viewModel.hideKeyboardEvent.observe(this) {
            hideKeyboard()
        }

        viewModel.isClearIconVisibile.observe(this) {
            binding.clearIcon.isVisible = it
        }
    }

    private fun clearListOnClick() {
        searchAdapter.clearTracks()
        binding.containerHistory.isVisible = true
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
        binding.listTracks.adapter = searchAdapter
        searchAdapter.updateTracks(tracks)
        binding.listTracks.isVisible = true
        binding.progressStatus.isVisible = false
        binding.containerHistory.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderUploadFailed.isVisible = false
        binding.buttonUpdate.isVisible = false
    }

    private fun showHistory(tracks: List<Track>) {
        binding.listTracksHistory.adapter = historyAdapter
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputValue = savedInstanceState.getString(SAVE_STATE).toString()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
    }

    private fun movePlayerActivity(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra(TRACK, track)
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val TRACK = "track"
        private const val SAVE_STATE = "save_state"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}