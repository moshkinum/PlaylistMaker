package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val ivArrowBack = findViewById<ImageView>(R.id.ivArrowBack)
        val ivCover: ImageView = findViewById<ImageView>(R.id.ivCover)
        val tvTrackName = findViewById<TextView>(R.id.tvTrackName)
        val tvArtistName = findViewById<TextView>(R.id.tvArtistName)
        val tvPlayTime = findViewById<TextView>(R.id.tvPlayTime)
        val tvDuration = findViewById<TextView>(R.id.tvDuration)
        val tvAlbumTitle = findViewById<TextView>(R.id.tvAlbumTitle)
        val tvAlbum = findViewById<TextView>(R.id.tvAlbum)
        val tvYear = findViewById<TextView>(R.id.tvYear)
        val tvGenre = findViewById<TextView>(R.id.tvGenre)
        val tvCountry = findViewById<TextView>(R.id.tvCountry)

        val json = intent.getStringExtra(CLICKED_TRACK)
        val track = Gson().fromJson(json, Track::class.java)

        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvPlayTime.text = "0:30"
        tvDuration.text = Functions.formatTimeMillis(track.trackTimeMillis)

        if (track.collectionName.isNullOrEmpty()) {
            tvAlbumTitle.isVisible = false
            tvAlbum.isVisible = false
        } else {
            tvAlbum.text = track.collectionName
        }

        tvYear.text = track.releaseDate.substring(0, 4)
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        val cornerRadius = 8f
        Glide.with(applicationContext)
            .load(Functions.getCoverArtwork(track.artworkUrl100))
            .transform(RoundedCorners(Functions.dpToPx(cornerRadius, applicationContext)))
            .placeholder(R.drawable.placeholder)
            .into(ivCover)

        ivArrowBack.setOnClickListener {
            finish()
        }
    }
}