package com.example.bcsd_android_2025_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MusicAdapter(
    private val musicList: List<MusicData>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(music: MusicData)
    }

    class MusicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.textTitle)
        val artistText: TextView = view.findViewById(R.id.textArtist)
        val durationText: TextView = view.findViewById(R.id.textDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun getItemCount() = musicList.size

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = musicList[position]
        holder.titleText.text = music.title
        holder.artistText.text = music.artist
        holder.durationText.text = formatDuration(music.duration)
        holder.itemView.setOnClickListener {
            listener.onItemClick(music)
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSec = durationMs / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%02d:%02d", min, sec)
    }
}
