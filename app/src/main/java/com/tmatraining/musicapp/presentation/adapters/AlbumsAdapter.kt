package com.tmatraining.musicapp.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.databinding.ItemAlbumBinding

class AlbumsAdapter(private val data: List<Album>, private val onClick: (Album) -> Unit) :
    RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>() {
    inner class AlbumsViewHolder(binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.albumTitle
        val artist = binding.albumArtist
        val image = binding.albumThumbnail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumsViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.artist.text = data[position].artist
        holder.image.setImageURI(Uri.parse(data[position].cover))

        holder.itemView.setOnClickListener {
            onClick(data[position])
        }
    }
}