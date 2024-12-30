package com.practicum.playlistmaker.search.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.media_library.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchHistoryRepositoryImpl(
    context: Context,
    private val gson: Gson,
    private val appDatabase: AppDatabase
) : SearchHistoryRepository {

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
            listSearchHistory.removeAt(listSearchHistory.lastIndex)
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
            CoroutineScope(Dispatchers.IO).launch {
                val favoriteTrackIds = appDatabase.favoriteTracksDao().getFavoriteIdList()

                val data = createListTracksFromJson(jsonTracks).map {
                    val isFavorite = it.trackId in favoriteTrackIds

                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl,
                        isFavorite = isFavorite
                    )
                }
                listSearchHistory.addAll(data)
            }
        }
        return listSearchHistory
    }

    private fun createListTracksFromJson(json: String): List<Track> {
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun createJsonFromListTracks(list: MutableList<Track>): String {
        return gson.toJson(list)
    }

    private companion object {
        const val TRACKS_PREFERENCES = "tracks_preferences"
        const val NEW_LIST_TRACK_KEY = "new_list_track_key"
        const val MAX_SIZE_LIST = 10
    }
}