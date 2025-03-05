package com.tmatraining.musicapp.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.databinding.ItemArtistBinding

class ArtistsAdapter(private val data: List<Artist>, private val onClick: (Artist) -> Unit) : RecyclerView.Adapter<ArtistsAdapter.ArtistsViewHolder>() {
    inner class ArtistsViewHolder(binding: ItemArtistBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.artistName
        val image = binding.artistImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistsViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ArtistsViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.image.setImageURI(Uri.parse(data[position].avatar))

        holder.itemView.setOnClickListener {
            onClick(data[position])
        }
    }
}