package com.tmatraining.musicapp.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.FragmentTracksBinding
import com.tmatraining.musicapp.presentation.activities.PlayerActivity
import com.tmatraining.musicapp.presentation.adapters.TracksAdapter
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class TracksFragment : Fragment() {
    companion object {
        private const val TAG = "TracksFragment"
    }

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: TracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)

        adapter = TracksAdapter(mutableListOf()) { song ->
            Intent(requireContext(), PlayerActivity::class.java).apply {
                PlaybackState.currentList.value = viewModel.tracks.value
                PlaybackState.currentSong.value = song
                PlaybackState.isPlaying.value = false
                Log.i(TAG, "onClick: ${PlaybackState.isPlaying.value}")
                startActivity(this)
            }
        }

        binding.playingListRecyclerView.apply {
            this.adapter = this@TracksFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emitLoadingState()
        viewModel.tracks.observe(viewLifecycleOwner) { songs ->
            adapter.updateData(songs)
            if (songs.isNotEmpty()) {
                emitLoadSuccessState()
            } else {
                emitEmptyDataState()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter.itemCount > 0) {
            emitLoadSuccessState()
        } else {
            emitEmptyDataState()
        }
        PlaybackState.currentSong.observe(viewLifecycleOwner) {
            binding.playingListRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun emitLoadSuccessState() {
        binding.playingListRecyclerView.visibility = View.VISIBLE
        binding.circularProgressIndicator.visibility = View.GONE
        binding.textView.visibility = View.GONE
    }

    private fun emitEmptyDataState() {
        binding.playingListRecyclerView.visibility = View.GONE
        binding.circularProgressIndicator.visibility = View.GONE
        binding.textView.visibility = View.VISIBLE
    }

    private fun emitLoadingState() {
        binding.playingListRecyclerView.visibility = View.GONE
        binding.circularProgressIndicator.visibility = View.VISIBLE
        binding.textView.visibility = View.GONE
    }
}
