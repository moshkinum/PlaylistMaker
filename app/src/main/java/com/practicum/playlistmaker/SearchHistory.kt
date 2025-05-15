package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val TRACKS_HISTORY_KEY = "tracks_history_key"
const val MAX_COUNT_TRACKS_HISTORY = 10

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    val tracksHistory = mutableListOf<Track>()

    fun read() {
        val json = sharedPrefs.getString(TRACKS_HISTORY_KEY, null)
        val tracks = Gson().fromJson(json, Array<Track>::class.java)
        tracksHistory.clear()
        if (tracks != null) {
            tracksHistory.addAll(tracks)
        }
    }

    fun write(track: Track) {
        with(tracksHistory) {
            val trackToRemove = find { it.trackId == track.trackId }
            if (trackToRemove != null) {
                remove(trackToRemove)
            }

            add(0, track)

            if (size > MAX_COUNT_TRACKS_HISTORY) {
                removeAt(lastIndex)
            }
        }

        val json = Gson().toJson(tracksHistory)
        sharedPrefs.edit()
            .putString(TRACKS_HISTORY_KEY, json)
            .apply()
    }

    fun clear() {
        tracksHistory.clear();
        sharedPrefs.edit()
            .remove(TRACKS_HISTORY_KEY)
            .apply()
    }
}