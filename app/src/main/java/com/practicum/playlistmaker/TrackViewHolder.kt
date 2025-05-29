package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)


    fun bind(track: Track) {
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvTrackTime.text = Functions.formatTimeMillis(track.trackTimeMillis)

        val cornerRadius = 2f
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(Functions.dpToPx(cornerRadius, itemView.context)))
            .into(ivCover)
    }

}