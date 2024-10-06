package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String = ""
    private val searchRunnable = Runnable {
        val newSearchText = latestSearchText
        search(newSearchText)
    }

    val tracks = mutableListOf<Track>()

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> get() = _state

    private val _isClearIconVisibile = MutableLiveData(false)
    val isClearIconVisibile: LiveData<Boolean> get() = _isClearIconVisibile

    private val _hideKeyboardEvent = MutableLiveData<Unit>()
    val hideKeyboardEvent: LiveData<Unit> get() = _hideKeyboardEvent

    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun onResponse(foundTracks: List<Track>) {
                    tracks.clear()
                    tracks.addAll(foundTracks)
                    hideKeyboard()

                    when {
                        tracks.isEmpty() -> renderState(SearchState.Empty)
                        else -> renderState(SearchState.SearchList(tracks = tracks))
                    }
                }

                override fun onFailure(t: Throwable) {
                    renderState(SearchState.Error)
                }
            })
        }
    }

    fun changeInputEditTextState(hasFocus: Boolean, input: String) {
        val searchHistory = searchHistoryInteractor.getSearchHistory()
        _isClearIconVisibile.postValue(input.isNotEmpty())
        if (hasFocus && input.isEmpty() && searchHistory.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
            _state.postValue(SearchState.HistoryList(searchHistory))
        } else {
            searchDebounce(input)
        }
    }

    fun saveTrackInHistory(track: Track) {
        searchHistoryInteractor.saveTrackInHistory(track)
    }

    fun isNotEmptySearchHistory(): Boolean {
        val searchHistory = searchHistoryInteractor.getSearchHistory()
        return if (searchHistory.isNotEmpty()) {
            _state.postValue(SearchState.HistoryList(searchHistory))
            true
        } else {
            false
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearListSearchHistory()
        _state.postValue(SearchState.HistoryList(emptyList()))
    }

    fun repeatRequest() {
        search(latestSearchText)
    }

    private fun renderState(state: SearchState) {
        _state.postValue(state)
    }

    private fun hideKeyboard() {
        _hideKeyboardEvent.postValue(Unit)
    }

    private fun searchDebounce(changedText: String) {
        latestSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        fun getViewModelFactory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SearchViewModel(
                        tracksInteractor = Creator.provideTracksInteractor(),
                        searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
                    )
                }
            }
        }
    }

}