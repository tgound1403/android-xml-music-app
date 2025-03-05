package com.tmatraining.musicapp.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.databinding.ItemSimilarBinding

class SimilarAdapter(private val data: List<Artist>, private val onClick: (Artist) -> Unit) : RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {
    inner class SimilarViewHolder(binding: ItemSimilarBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.artistName
        val image = binding.artistImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val binding = ItemSimilarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimilarViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.image.setImageURI(Uri.parse(data[position].avatar))

        holder.itemView.setOnClickListener {
            onClick(data[position])
        }
    }
}