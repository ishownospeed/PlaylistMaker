package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var latestSearchText: String = ""

    private var searchJob: Job? = null

    private val tracks = mutableListOf<Track>()

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> get() = _state

    private val _isClearIconVisibile = MutableLiveData(false)
    val isClearIconVisibile: LiveData<Boolean> get() = _isClearIconVisibile

    private val _hideKeyboardEvent = MutableLiveData<Unit>()
    val hideKeyboardEvent: LiveData<Unit> get() = _hideKeyboardEvent

    init {
        viewModelScope.launch {
            val searchHistory = searchHistoryInteractor.getSearchHistory()
            _state.postValue(SearchState.HistoryList(searchHistory))
        }
    }

    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { (foundTracks, errorMessage) ->
                        if (foundTracks != null) {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                            hideKeyboard()
                        }
                        when {
                            errorMessage != null -> renderState(SearchState.Error)
                            tracks.isEmpty() -> renderState(SearchState.Empty)
                            else -> renderState(SearchState.SearchList(tracks = tracks))
                        }
                    }
            }
        }
    }

    fun changeInputEditTextState(hasFocus: Boolean, input: String) {
        val searchHistory = searchHistoryInteractor.getSearchHistory()
        _isClearIconVisibile.postValue(input.isNotEmpty())

        when {
            input.isEmpty() && searchHistory.isNotEmpty() -> {
                _state.postValue(SearchState.HistoryList(searchHistory))
            }
            tracks.isNotEmpty() && latestSearchText == input -> {
                _state.postValue(SearchState.SearchList(tracks))
            }
            else -> searchDebounce(input)
        }
    }

    fun saveTrackInHistory(track: Track) {
        searchHistoryInteractor.saveTrackInHistory(track)
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

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            if (latestSearchText == changedText) {
                search(latestSearchText)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}