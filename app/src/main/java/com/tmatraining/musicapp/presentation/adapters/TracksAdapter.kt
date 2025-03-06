package com.tmatraining.musicapp.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.ItemTrackBinding

class TracksAdapter(private var data: MutableList<Song>, private val onClick: (Song) -> Unit) : RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {
    inner class TracksViewHolder(private val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songTitle
        val artist = binding.songArtist
        val albumArt = binding.songThumbnail
        val moveToTopButton = binding.moveToTopButton
        val deleteButton = binding.deleteButton
        val musicWave = binding.musicWave
        val waveBar1 = binding.waveBar1
        val waveBar2 = binding.waveBar2
        val waveBar3 = binding.waveBar3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TracksViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val song = data[position]
        holder.artist.text = song.artist
        holder.title.text = song.title

        val currentPlaying = PlaybackState.currentSong.value
        if (song == currentPlaying) {
            holder.itemView.findViewById<View>(R.id.active_border).visibility = View.VISIBLE
            holder.itemView.findViewById<CardView>(R.id.foreground_layout).cardElevation = 8f
            holder.musicWave.visibility = View.VISIBLE
        } else {
            holder.itemView.findViewById<View>(R.id.active_border).visibility = View.GONE
            holder.itemView.findViewById<CardView>(R.id.foreground_layout).cardElevation = 2f
            holder.musicWave.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onClick(song)
        }
        holder.albumArt.setImageURI(Uri.parse(song.albumArtUri))

        holder.moveToTopButton.setOnClickListener {
            moveToTop(position)
        }
        holder.deleteButton.setOnClickListener {
            deleteItem(position)
        }
    }

    private fun moveToTop(position: Int) {
        val item = data.removeAt(position)
        data.add(1, item)
        notifyDataSetChanged()
    }

    private fun deleteItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    fun updateData(newData: MutableList<Song>) {
        data = newData
        notifyDataSetChanged()
    }

    fun getData(): MutableList<Song> = data
}