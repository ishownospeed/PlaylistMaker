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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()

    private lateinit var buttonArrowLeft: ImageView
    private lateinit var clearButton: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var placeholderNothingFound: TextView
    private lateinit var placeholderUploadFailed: TextView
    private lateinit var buttonUpdate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        buttonArrowLeft = findViewById(R.id.arrowLeftSearch)
        clearButton = findViewById(R.id.clearIcon)
        inputEditText = findViewById(R.id.inputEditText)
        recycler = findViewById(R.id.listTracks)
        placeholderNothingFound = findViewById(R.id.placeholderNothingFound)
        placeholderUploadFailed = findViewById(R.id.placeholderUploadFailed)
        buttonUpdate = findViewById(R.id.buttonUpdate)

        adapter.tracks = tracks
        recycler.adapter = adapter

        buttonArrowLeft.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                inputValue = inputEditText.text.toString()
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
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val SAVE_STATE = "SAVE_STATE"
    }

}