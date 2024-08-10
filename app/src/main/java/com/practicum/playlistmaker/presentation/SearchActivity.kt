package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.TrackAdapter

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private var inputValue: String = ""

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }

    private val tracks = mutableListOf<Track>()
    private val searchHistory by lazy { Creator.provideSearchHistoryInteractor(this) }
    private lateinit var adapterSearchHistory: TrackAdapter

    private val adapterListTracks = TrackAdapter(tracks) {
        if (clickDebounce()) {
            searchHistory.saveTrackInHistory(it)
            adapterSearchHistory.updateTracks(searchHistory.getListSearchHistory())
            moveMediaLibraryActivity(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listTracks.adapter = adapterListTracks

        adapterSearchHistory = TrackAdapter(searchHistory.getListSearchHistory()) {
            moveMediaLibraryActivity(it)
        }
        adapterSearchHistory.updateTracks(searchHistory.getListSearchHistory())

        binding.listTracksHistory.adapter = adapterSearchHistory

        binding.buttonClearHistory.setOnClickListener {
            adapterSearchHistory.clearTracks()
            searchHistory.clearListSearchHistory()
            binding.containerHistory.visibility = View.GONE
        }

        binding.arrowLeftSearch.setOnClickListener { finish() }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            adapterListTracks.clearTracks()
            binding.placeholderNothingFound.visibility = View.GONE
            binding.placeholderUploadFailed.visibility = View.GONE
            binding.buttonUpdate.visibility = View.GONE
        }

        binding.containerHistory.isVisible = searchHistory.getListSearchHistory().isNotEmpty()

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            binding.containerHistory.isVisible =
                hasFocus && binding.inputEditText.text.isEmpty() && searchHistory.getListSearchHistory()
                    .isNotEmpty()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                inputValue = binding.inputEditText.text.toString()

                binding.containerHistory.isVisible =
                    binding.inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.getListSearchHistory()
                        .isNotEmpty()

                if (binding.containerHistory.visibility == View.VISIBLE) {
                    binding.listTracks.visibility = View.GONE
                } else {
                    binding.listTracks.visibility = View.VISIBLE
                    adapterListTracks.clearTracks()
                }

                if (binding.inputEditText.hasFocus() && s?.isEmpty() == true || searchHistory.getListSearchHistory()
                        .isNotEmpty()
                ) {
                    binding.placeholderNothingFound.visibility = View.GONE
                    binding.placeholderUploadFailed.visibility = View.GONE
                    binding.buttonUpdate.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        binding.inputEditText.addTextChangedListener(textWatcher)

    }

    private fun moveMediaLibraryActivity(track: Track) {
        val mediaLibraryIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
        mediaLibraryIntent.putExtra(TRACK, track)
        startActivity(mediaLibraryIntent)
    }

    private fun searchTrack() {
        val text = binding.inputEditText.text.toString()
        if (text.isNotEmpty()) {

            binding.placeholderNothingFound.visibility = View.GONE
            binding.placeholderUploadFailed.visibility = View.GONE
            binding.buttonUpdate.visibility = View.GONE
            binding.listTracks.visibility = View.GONE
            binding.progressStatus.visibility = View.VISIBLE

            Creator.provideTracksInteractor()
                .searchTracks(text, object : TracksInteractor.TracksConsumer {
                    override fun onResponse(foundTracks: List<Track>) {
                        runOnUiThread {
                            binding.progressStatus.visibility = View.GONE

                            if (foundTracks.isNotEmpty()) {
                                tracks.clear()
                                adapterListTracks.addAllTracks(foundTracks)
                                binding.listTracks.visibility = View.VISIBLE
                                binding.placeholderNothingFound.visibility = View.GONE
                                binding.placeholderUploadFailed.visibility = View.GONE
                                binding.buttonUpdate.visibility = View.GONE
                            } else {
                                binding.placeholderNothingFound.visibility = View.VISIBLE
                                binding.placeholderUploadFailed.visibility = View.GONE
                                binding.buttonUpdate.visibility = View.GONE
                                binding.progressStatus.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(t: Throwable) {
                        runOnUiThread {
                            binding.progressStatus.visibility = View.GONE
                            showPlaceholderError()
                        }
                    }
                })
        }
    }

    private fun showPlaceholderError() {
        tracks.clear()
        binding.progressStatus.visibility = View.GONE
        binding.placeholderNothingFound.visibility = View.GONE
        binding.placeholderUploadFailed.visibility = View.VISIBLE
        binding.buttonUpdate.visibility = View.VISIBLE

        binding.buttonUpdate.setOnClickListener {
            searchTrack()
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_STATE, inputValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputValue = savedInstanceState.getString(SAVE_STATE).toString()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SAVE_STATE = "SAVE_STATE"
        const val TRACK = "track"
    }
}