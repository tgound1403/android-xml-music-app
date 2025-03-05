package com.tmatraining.musicapp.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.databinding.ItemPlaylistBinding

class PlaylistsAdapter(private val data: List<Album>, private val onClick: (Album) -> Unit) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder>() {
    inner class PlaylistsViewHolder(binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.playlistName
        val contributors = binding.contributors
        val image = binding.playlistImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistsViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.contributors.text = data[position].artist
        holder.image.setImageURI(Uri.parse(data[position].cover))
    }
}