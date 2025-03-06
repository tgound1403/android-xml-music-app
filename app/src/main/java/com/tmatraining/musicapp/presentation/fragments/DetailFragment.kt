package com.tmatraining.musicapp.presentation.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.FragmentDetailBinding
import com.tmatraining.musicapp.presentation.adapters.SimilarAdapter
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.time.Duration.Companion.milliseconds

@FragmentScoped
class DetailFragment : Fragment() {
    private lateinit var _binding: FragmentDetailBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var data: Song
    private lateinit var artistList: List<Artist>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        data = PlaybackState.currentSong.value!!
        artistList = viewModel.artists.value ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.apply {
            songTitle.text = data.title
            songArtist.text = data.artist
            songAlbum.text = data.album
            songDuration.text = data.duration.milliseconds.toString()
            similarTitle.text = getString(R.string.similarArtists)
            cover.setImageURI(Uri.parse(data.albumArtUri))
            similarList.apply {
                adapter = SimilarAdapter(artistList.filter { e -> e.name != data.artist }) {}
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }
}