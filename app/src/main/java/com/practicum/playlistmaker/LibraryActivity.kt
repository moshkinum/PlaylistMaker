package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val tbLibrary = findViewById<MaterialToolbar>(R.id.tbLibrary)
        tbLibrary.setNavigationOnClickListener {
            finish()
        }
    }
}