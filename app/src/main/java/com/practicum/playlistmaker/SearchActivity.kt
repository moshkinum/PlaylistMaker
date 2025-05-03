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

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = mutableListOf<Track>()

    private lateinit var adapter: TracksAdapter
    private lateinit var tbSearch: MaterialToolbar
    private lateinit var etSearch: EditText
    private lateinit var ivClearSearch: ImageView
    private lateinit var ivError: ImageView
    private lateinit var tvError: TextView
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

        tbSearch = findViewById(R.id.tbSearch)
        etSearch = findViewById(R.id.etSearch)
        ivClearSearch = findViewById(R.id.ivClearSearch)
        ivError = findViewById(R.id.ivError)
        tvError = findViewById(R.id.tvError)
        btnRefresh = findViewById(R.id.btnRefresh)

        tbSearch.setNavigationOnClickListener {
            finish()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        ivClearSearch.setOnClickListener {
            etSearch.setText("")
            if (tracks.isNotEmpty()) {
                tracks.clear()
                adapter.notifyDataSetChanged()
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
                searchText = s.toString()
            }

            override fun afterTextChanged(
                s: Editable?,
            ) {
            }
        }
        etSearch.addTextChangedListener(searchWatcher)
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
            .search(etSearch.text.toString())
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
        ivError.visibility = View.VISIBLE
        tvError.visibility = View.VISIBLE

        if (errorType == ErrorType.NOTHING_FOUND) {
            ivError.setImageResource(R.drawable.nothing_found)
            tvError.text = getString(R.string.nothing_found)
            btnRefresh.visibility = View.GONE
        } else if (errorType == ErrorType.NO_CONNECTION) {
            ivError.setImageResource(R.drawable.no_connection)
            tvError.text = getString(R.string.no_connection)
            btnRefresh.visibility = View.VISIBLE
        }
    }

    private fun clearError() {
        ivError.visibility = View.GONE
        tvError.visibility = View.GONE
        btnRefresh.visibility = View.GONE
    }

}