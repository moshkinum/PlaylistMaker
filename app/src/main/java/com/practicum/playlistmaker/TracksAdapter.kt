package com.practicum.adapter_lesson_3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchHistory
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.TracksViewHolder

class TracksAdapter(
    private val tracks: List<Track>,
    private val searchHistory: SearchHistory,
) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TracksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TracksViewHolder,
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