package com.practicum.playlistmaker.data.impl

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(context: Context) : SearchHistoryRepository {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(TRACKS_PREFERENCES, MODE_PRIVATE)

    private var listSearchHistory = mutableListOf<Track>()

    init {
        listSearchHistory = getListSearchHistory()
    }

    override fun clearListSearchHistory() {
        listSearchHistory.clear()
        sharedPrefs.edit().remove(NEW_LIST_TRACK_KEY).apply()
    }

    override fun saveTrackInHistory(item: Track) {
        listSearchHistory.removeIf { it.trackId == item.trackId }
        if (listSearchHistory.size == MAX_SIZE_LIST) {
            listSearchHistory.removeLast()
        }
        listSearchHistory.add(0, item)

        sharedPrefs.edit()
            .putString(NEW_LIST_TRACK_KEY, createJsonFromListTracks(listSearchHistory))
            .apply()
    }

    override fun getSearchHistory(): MutableList<Track> {
        return listSearchHistory
    }

    private fun getListSearchHistory(): MutableList<Track> {
        val jsonTracks = sharedPrefs.getString(NEW_LIST_TRACK_KEY, null)
        if (jsonTracks != null) {
            listSearchHistory.addAll(createListTracksFromJson(jsonTracks))
        }
        return listSearchHistory
    }

    private fun createListTracksFromJson(json: String): MutableList<Track> {
        return try {
            Gson().fromJson(json, Array<Track>::class.java).toMutableList()
        } catch (e: JsonSyntaxException) {
            val track = Gson().fromJson(json, Track::class.java)
            mutableListOf(track)
        }
    }

    private fun createJsonFromListTracks(list: MutableList<Track>): String {
        return Gson().toJson(list)
    }

    private companion object {
        const val TRACKS_PREFERENCES = "tracks_preferences"
        const val NEW_LIST_TRACK_KEY = "new_list_track_key"
        const val MAX_SIZE_LIST = 10
    }
}