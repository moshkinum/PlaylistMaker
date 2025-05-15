package com.practicum.adapter_lesson_3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchHistory
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.TrackViewHolder

class TrackAdapter(
    private val tracks: List<Track>,
    private val searchHistory: SearchHistory,
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int,
    ) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            searchHistory.write(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}