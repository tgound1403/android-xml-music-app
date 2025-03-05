package com.tmatraining.musicapp.presentation.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Artist
import com.tmatraining.musicapp.databinding.FragmentArtistsBinding
import com.tmatraining.musicapp.presentation.adapters.ArtistsAdapter
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class ArtistsFragment : Fragment() {
    private lateinit var _binding: FragmentArtistsBinding
    private val binding get() = _binding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var artistLayoutManager: GridLayoutManager
    private var data = emptyList<Artist>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        data = viewModel.artists.value ?: emptyList()
    }

    override fun onStart() {
        super.onStart()
        binding.artistsRecyclerView.apply {
            adapter = ArtistsAdapter(data) {
                viewModel.getSongByArtist(it.name)
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, SongsFragment(Factor.ARTIST)).addToBackStack(null)
                    .commit()
            }
            layoutManager = artistLayoutManager
        }
        data = viewModel.artists.value ?: emptyList()
    }

    override fun onResume() {
        super.onResume()
        if (data.isNotEmpty()) {
            emitLoadSuccessState()
        } else {
            emitEmptyDataState()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        artistLayoutManager = GridLayoutManager(this@ArtistsFragment.requireContext(), getSpanCount())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        artistLayoutManager.spanCount = getSpanCount(newConfig.orientation)
    }


    private fun getSpanCount(orientation: Int = resources.configuration.orientation): Int {
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            4
        } else {
            2
        }
    }

    private fun emitLoadSuccessState() {
        binding.artistsRecyclerView.visibility = View.VISIBLE
        binding.circularProgressIndicator.visibility = View.GONE
        binding.textView.visibility = View.GONE
    }

    private fun emitEmptyDataState() {
        binding.artistsRecyclerView.visibility = View.GONE
        binding.circularProgressIndicator.visibility = View.GONE
        binding.textView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding.artistsRecyclerView.adapter = null
        _binding.artistsRecyclerView.layoutManager = null
    }
}