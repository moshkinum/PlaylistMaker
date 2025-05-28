package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.adapter_lesson_3.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SEARCH_TEXT = "SEARCH_TEXT"
const val DEF_TEXT = ""
const val CLICKED_TRACK = "CLICKED_TRACK"

enum class ErrorType {
    NOTHING_FOUND, NO_CONNECTION
}

class SearchActivity : AppCompatActivity() {

    private lateinit var tbSearch: MaterialToolbar
    private lateinit var etSearch: EditText
    private lateinit var ivClearSearch: ImageView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var ivError: ImageView
    private lateinit var tvError: TextView
    private lateinit var btnRefresh: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var llHistory: LinearLayout
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var btnClearHistory: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = mutableListOf<Track>()

    private var searchText: String = DEF_TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        tbSearch = findViewById(R.id.tbSearch)
        etSearch = findViewById(R.id.etSearch)
        ivClearSearch = findViewById(R.id.ivClearSearch)
        ivError = findViewById(R.id.ivError)
        tvError = findViewById(R.id.tvError)
        btnRefresh = findViewById(R.id.btnRefresh)
        llHistory = findViewById<LinearLayout>(R.id.llHistory)
        btnClearHistory = findViewById<Button>(R.id.btnClearHistory)

        searchHistory = SearchHistory((applicationContext as App).sharedPrefs)

        val rwTracks = findViewById<RecyclerView>(R.id.rwTracks)
        trackAdapter = TrackAdapter(tracks, onTrackClickListener)
        rwTracks.adapter = trackAdapter

        val rwHistory = findViewById<RecyclerView>(R.id.rwHistory)
        historyAdapter = TrackAdapter(searchHistory.tracksHistory, onTrackClickListener)
        rwHistory.adapter = historyAdapter

        tbSearch.setNavigationOnClickListener {
            finish()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        etSearch.setOnFocusChangeListener { _, hasFocus ->
            llHistory.isVisible = false

            if (hasFocus and etSearch.text.isEmpty()) {
                searchHistory.read()
                historyAdapter.notifyDataSetChanged()
                llHistory.isVisible = searchHistory.tracksHistory.isNotEmpty()
            }
        }

        ivClearSearch.setOnClickListener {
            etSearch.setText("")
            if (tracks.isNotEmpty()) {
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
            }
            clearError()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                etSearch.windowToken,
                0
            )
        }

        btnRefresh.setOnClickListener {
            search()
        }

        val searchWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {
                ivClearSearch.isVisible = !s.isNullOrEmpty()
                llHistory.isVisible = etSearch.hasFocus() and s.isNullOrEmpty()
                searchText = s.toString()
            }

            override fun afterTextChanged(
                s: Editable?,
            ) {
            }
        }
        etSearch.addTextChangedListener(searchWatcher)

        btnClearHistory.setOnClickListener {
            searchHistory.clear()
            llHistory.isVisible = false
        }
    }

    override fun onStart() {
        super.onStart()
        if (llHistory.isVisible || etSearch.text.isNotEmpty()) {
            searchHistory.read()
            historyAdapter.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, DEF_TEXT)
    }

    private fun search() {
        iTunesService
            .search(etSearch.text.toString())
            .enqueue(
                (object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (tracks.isNotEmpty()) {
                            tracks.clear()
                            trackAdapter.notifyDataSetChanged()
                        }
                        clearError()

                        if (response.code() == 200) {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }

                            if (tracks.isEmpty()) {
                                showError(ErrorType.NOTHING_FOUND)
                            }
                        } else {
                            showError(ErrorType.NO_CONNECTION)
                        }
                    }

                    override fun onFailure(p0: Call<TrackResponse>, p1: Throwable) {
                        if (tracks.isNotEmpty()) {
                            tracks.clear()
                            trackAdapter.notifyDataSetChanged()
                        }
                        showError(ErrorType.NO_CONNECTION)
                    }

                })
            )
    }

    private fun showError(errorType: ErrorType) {
        ivError.isVisible = true
        tvError.isVisible = true

        if (errorType == ErrorType.NOTHING_FOUND) {
            ivError.setImageResource(R.drawable.nothing_found)
            tvError.text = getString(R.string.nothing_found)
            btnRefresh.isVisible = false
        } else if (errorType == ErrorType.NO_CONNECTION) {
            ivError.setImageResource(R.drawable.no_connection)
            tvError.text = getString(R.string.no_connection)
            btnRefresh.isVisible = true
        }
    }

    private fun clearError() {
        ivError.isVisible = false
        tvError.isVisible = false
        btnRefresh.isVisible = false
    }

    private val onTrackClickListener: (Track) -> Unit = { track ->
        searchHistory.write(track)
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(CLICKED_TRACK, Gson().toJson(track))
        startActivity(intent)
    }

}