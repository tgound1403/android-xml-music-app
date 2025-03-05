package com.tmatraining.musicapp.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmatraining.musicapp.core.db.entity.Album
import com.tmatraining.musicapp.databinding.FragmentPlaylistsBinding
import com.tmatraining.musicapp.presentation.adapters.PlaylistsAdapter
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class PlaylistsFragment : Fragment() {
    private lateinit var _binding: FragmentPlaylistsBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var data = emptyList<Album>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        data = viewModel.playlists.value ?: emptyList()
    }

    override fun onStart() {
        super.onStart()
        Log.i(this.javaClass.simpleName, "onStart")
        _binding.playingListRecyclerView.apply {
            adapter = PlaylistsAdapter(data) {
                Log.i(this.javaClass.simpleName, "onClick: $it")
            }
            layoutManager = LinearLayoutManager(this@PlaylistsFragment.requireContext())
        }
        data = viewModel.playlists.value ?: emptyList()
    }

    override fun onStop() {
        super.onStop()
        Log.i(this.javaClass.simpleName, "onStop")
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
        Log.i(this.javaClass.simpleName, "onViewCreated get data: $data")
    }

    override fun onPause() {
        super.onPause()
        Log.i(this.javaClass.simpleName, "onPause")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    private fun emitLoadSuccessState() {
        _binding.playingListRecyclerView.visibility = View.VISIBLE
        _binding.circularProgressIndicator.visibility = View.GONE
        _binding.textView.visibility = View.GONE
    }

    private fun emitEmptyDataState() {
        _binding.playingListRecyclerView.visibility = View.GONE
        _binding.circularProgressIndicator.visibility = View.GONE
        _binding.textView.visibility = View.VISIBLE

    }
}