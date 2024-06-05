package com.practicum.playlistmaker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class SearchHistory(
    context: Context,
    private var listSearchHistory: MutableList<Track>
) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(TRACKS_PREFERENCES, MODE_PRIVATE)

    init {
        listSearchHistory = getSearchHistory()
    }

    fun getListSearchHistory(): MutableList<Track> = listSearchHistory

    fun clearListSearchHistory() {
        listSearchHistory.clear()
        sharedPrefs.edit().remove(NEW_LIST_TRACK_KEY).apply()
    }

    fun saveTrackInHistory(item: Track) {
        listSearchHistory.removeIf { it.trackId == item.trackId }
        if (listSearchHistory.size == MAX_SIZE_LIST) {
            listSearchHistory.removeLast()
        }
        listSearchHistory.add(0, item)

        sharedPrefs.edit()
            .putString(NEW_LIST_TRACK_KEY, createJsonFromListTracks(listSearchHistory))
            .apply()
    }

    private fun getSearchHistory(): MutableList<Track> {
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

    companion object {
        private const val TRACKS_PREFERENCES = "tracks_preferences"
        private const val NEW_LIST_TRACK_KEY = "new_list_track_key"
        private const val MAX_SIZE_LIST = 10
    }
}