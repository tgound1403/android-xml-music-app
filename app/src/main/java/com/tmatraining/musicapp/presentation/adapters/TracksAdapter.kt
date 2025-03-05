package com.tmatraining.musicapp.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.databinding.ItemTrackBinding

class TracksAdapter(private var data: List<Song>, private val onClick: (Song) -> Unit) : RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {
    inner class TracksViewHolder(private val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songTitle
        val artist = binding.songArtist
        val moreBtn = binding.moreBtn
        val albumArt = binding.songThumbnail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TracksViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.artist.text = data[position].artist
        holder.title.text = data[position].title
        holder.moreBtn.setImageResource(R.drawable.more_horiz_24px)

        holder.itemView.setOnClickListener {
            onClick(data[position])
        }
        holder.albumArt.setImageURI(Uri.parse(data[position].albumArtUri))
    }

    fun updateData(newData: List<Song>) {
        data = newData
        notifyDataSetChanged()
    }
}