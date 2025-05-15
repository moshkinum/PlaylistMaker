package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_MAKER_PREFS = "playlist_maker_prefs"
const val IS_DARK_THEME_KEY = "is_dark_theme_key"

class App : Application() {

    lateinit var sharedPrefs: SharedPreferences
    var isDarkTheme = false

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFS, MODE_PRIVATE)
        isDarkTheme = sharedPrefs.getBoolean(IS_DARK_THEME_KEY, false)
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun setTheme(isDarkTheme: Boolean) {
        this.isDarkTheme = isDarkTheme
        switchTheme(isDarkTheme)
        sharedPrefs.edit()
            .putBoolean(IS_DARK_THEME_KEY, isDarkTheme)
            .apply()
    }

}