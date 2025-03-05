package com.tmatraining.musicapp.presentation.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.databinding.FragmentAlbumsBinding
import com.tmatraining.musicapp.presentation.adapters.AlbumsAdapter
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.scopes.FragmentScoped


@FragmentScoped
class AlbumsFragment() : Fragment() {
    private lateinit var _binding: FragmentAlbumsBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var albumsLayoutManager: GridLayoutManager? = null
    private var data = emptyList<Album>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        data = viewModel.albums.value ?: emptyList()
    }

    override fun onStart() {
        super.onStart()
        Log.i(this.javaClass.simpleName, "onStart")
        _binding.albumsRecyclerView.apply {
            adapter = AlbumsAdapter(data) {
                viewModel.getSongByAlbum(it.name)
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, SongsFragment(Factor.ALBUM)).addToBackStack(null)
                    .commit()
            }
            layoutManager = albumsLayoutManager
        }
        data = viewModel.albums.value ?: emptyList()
    }

    override fun onResume() {
        super.onResume()
        Log.i(this.javaClass.simpleName, "onResume")
        if (data.isNotEmpty()) {
            emitLoadSuccessState()
        } else {
            emitEmptyDataState()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumsLayoutManager = GridLayoutManager(this@AlbumsFragment.requireContext(), getSpanCount())
    }

    override fun onPause() {
        super.onPause()
        Log.i(this.javaClass.simpleName, "onPause")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        albumsLayoutManager?.spanCount = getSpanCount()
    }

    private fun getSpanCount(): Int {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            4
        } else {
            2
        }
    }

    private fun emitLoadSuccessState() {
        _binding.albumsRecyclerView.visibility = View.VISIBLE
        _binding.circularProgressIndicator.visibility = View.GONE
        _binding.textView.visibility = View.GONE
    }

    private fun emitEmptyDataState() {
        _binding.albumsRecyclerView.visibility = View.GONE
        _binding.circularProgressIndicator.visibility = View.GONE
        _binding.textView.visibility = View.VISIBLE

    }
}