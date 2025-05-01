package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.adapter_lesson_3.TracksAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

enum class ErrorType {
    NOTHING_FOUND, NO_CONNECTION
}

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = mutableListOf<Track>()

    private lateinit var adapter: TracksAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var inputSearch: EditText
    private lateinit var iconClear: ImageView
    private lateinit var textError: TextView
    private lateinit var imageError: ImageView
    private lateinit var btnRefresh: Button

    private var searchText: String = TEXT_DEF

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = TracksAdapter(tracks)
        recyclerView.adapter = adapter

        toolbar = findViewById(R.id.toolbar)
        inputSearch = findViewById(R.id.inputSearch)
        iconClear = findViewById(R.id.iconClear)
        textError = findViewById(R.id.textError)
        imageError = findViewById(R.id.imageError)
        btnRefresh = findViewById(R.id.btnRefresh)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        iconClear.setOnClickListener {
            inputSearch.setText("")
            if (tracks.isNotEmpty()) {
                tracks.clear()
                adapter.notifyDataSetChanged()
            }
            clearError()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(
                inputSearch.windowToken,
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
                iconClear.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
            }

            override fun afterTextChanged(
                s: Editable?,
            ) {
            }
        }
        inputSearch.addTextChangedListener(searchWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEF)
    }

    private fun search() {
        iTunesService
            .search(inputSearch.text.toString())
            .enqueue(
                (object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        if (tracks.isNotEmpty()) {
                            tracks.clear()
                            adapter.notifyDataSetChanged()
                        }
                        clearError()

                        if (response.code() == 200) {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            }

                            if (tracks.isEmpty()) {
                                showError(ErrorType.NOTHING_FOUND)
                            }
                        } else {
                            showError(ErrorType.NO_CONNECTION)
                        }
                    }

                    override fun onFailure(p0: Call<TracksResponse>, p1: Throwable) {
                        if (tracks.isNotEmpty()) {
                            tracks.clear()
                            adapter.notifyDataSetChanged()
                        }
                        showError(ErrorType.NO_CONNECTION)
                    }

                })
            )
    }

    private fun showError(errorType: ErrorType) {
        imageError.visibility = View.VISIBLE
        textError.visibility = View.VISIBLE

        if (errorType == ErrorType.NOTHING_FOUND) {
            imageError.setImageResource(R.drawable.nothing_found)
            textError.text = getString(R.string.nothing_found)
            btnRefresh.visibility = View.GONE
        } else if (errorType == ErrorType.NO_CONNECTION) {
            imageError.setImageResource(R.drawable.no_connection)
            textError.text = getString(R.string.no_connection)
            btnRefresh.visibility = View.VISIBLE
        }
    }

    private fun clearError() {
        imageError.visibility = View.GONE
        textError.visibility = View.GONE
        btnRefresh.visibility = View.GONE
    }

}