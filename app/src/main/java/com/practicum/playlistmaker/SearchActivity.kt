package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var inputValue: String = ""

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(iTunesApi::class.java)

    private val tracks = mutableListOf<Track>()
    private lateinit var searchHistory: SearchHistory
    private lateinit var adapterSearchHistory: TrackAdapter

    private val adapterListTracks = TrackAdapter(tracks) {
        searchHistory.saveTrackInHistory(it)
        adapterSearchHistory.updateTracks(searchHistory.getListSearchHistory())
    }

    private lateinit var buttonArrowLeft: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var recyclerListTracks: RecyclerView
    private lateinit var placeholderNothingFound: TextView
    private lateinit var placeholderUploadFailed: TextView
    private lateinit var buttonUpdate: TextView
    private lateinit var containerHistory: LinearLayout
    private lateinit var recyclerListTracksHistory: RecyclerView
    private lateinit var buttonClearHistory: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        buttonArrowLeft = findViewById(R.id.arrowLeftSearch)
        clearButton = findViewById(R.id.clearIcon)
        inputEditText = findViewById(R.id.inputEditText)
        recyclerListTracks = findViewById(R.id.listTracks)
        placeholderNothingFound = findViewById(R.id.placeholderNothingFound)
        placeholderUploadFailed = findViewById(R.id.placeholderUploadFailed)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        containerHistory = findViewById(R.id.containerHistory)
        recyclerListTracksHistory = findViewById(R.id.listTracksHistory)
        buttonClearHistory = findViewById(R.id.buttonClearHistory)

        recyclerListTracks.adapter = adapterListTracks

        searchHistory = SearchHistory(this, mutableListOf())
        adapterSearchHistory = TrackAdapter(searchHistory.getListSearchHistory()) {}
        adapterSearchHistory.updateTracks(searchHistory.getListSearchHistory())

        recyclerListTracksHistory.adapter = adapterSearchHistory

        buttonClearHistory.setOnClickListener {
            adapterSearchHistory.clearTracks()
            searchHistory.clearListSearchHistory()
            containerHistory.visibility = View.GONE
        }

        buttonArrowLeft.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            adapterListTracks.clearTracks()
            placeholderNothingFound.visibility = View.GONE
            placeholderUploadFailed.visibility = View.GONE
            buttonUpdate.visibility = View.GONE
        }

        containerHistory.isVisible = searchHistory.getListSearchHistory().isNotEmpty()

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            containerHistory.isVisible =
                hasFocus && inputEditText.text.isEmpty() && searchHistory.getListSearchHistory()
                    .isNotEmpty()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                inputValue = inputEditText.text.toString()

                containerHistory.isVisible =
                    inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.getListSearchHistory()
                        .isNotEmpty()

                if (containerHistory.visibility == View.VISIBLE) {
                    recyclerListTracks.visibility = View.GONE
                } else {
                    recyclerListTracks.visibility = View.VISIBLE
                    adapterListTracks.clearTracks()
                }

                if (inputEditText.hasFocus() && s?.isEmpty() == true || searchHistory.getListSearchHistory()
                        .isNotEmpty()
                ) {
                    placeholderNothingFound.visibility = View.GONE
                    placeholderUploadFailed.visibility = View.GONE
                    buttonUpdate.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(textWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    responseServer()
                }
                true
            }
            false
        }
    }

    private fun responseServer() {
        trackService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == SUCCESS_OK) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            adapterListTracks.addAllTracks(response.body()?.results!!)
                            placeholderNothingFound.visibility = View.GONE
                            placeholderUploadFailed.visibility = View.GONE
                            buttonUpdate.visibility = View.GONE
                        }
                        if (tracks.isEmpty()) {
                            placeholderNothingFound.visibility = View.VISIBLE
                            placeholderUploadFailed.visibility = View.GONE
                            buttonUpdate.visibility = View.GONE
                        }
                    } else {
                        showPlaceholderError()
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showPlaceholderError()
                }

            })
    }

    private fun showPlaceholderError() {
        tracks.clear()
        placeholderNothingFound.visibility = View.GONE
        placeholderUploadFailed.visibility = View.VISIBLE
        buttonUpdate.visibility = View.VISIBLE

        buttonUpdate.setOnClickListener {
            responseServer()
        }
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
        private const val SAVE_STATE = "SAVE_STATE"
        private const val SUCCESS_OK = 200
    }
}