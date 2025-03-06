package com.tmatraining.musicapp.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.FragmentSongsBinding
import com.tmatraining.musicapp.presentation.activities.PlayerActivity
import com.tmatraining.musicapp.presentation.adapters.TracksAdapter
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class SongsFragment(private val factor: Factor) : Fragment() {
    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: TracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)

        adapter = TracksAdapter(mutableListOf()) { song ->
            Intent(requireContext(), PlayerActivity::class.java).apply {
                PlaybackState.currentList.value = viewModel.songsByFactor.value
                PlaybackState.currentSong.value = song
                startActivity(this)
            }
        }

        binding.playingListRecyclerView.apply {
            this.adapter = this@SongsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emitLoadingState()
        binding.apply {
            backBtn.setImageResource(R.drawable.chevron_left_24px)
            backBtn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
        viewModel.songsByFactor.observe(viewLifecycleOwner) { songs ->
            binding.title.text = if (factor == Factor.ARTIST) {
                "Songs by ${viewModel.songsByFactor.value?.get(0)?.artist}"
            } else {
                "Songs in ${viewModel.songsByFactor.value?.get(0)?.album}"
            }
            adapter.updateData(songs.toMutableList())
            emitLoadSuccessState()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun emitLoadSuccessState() {
        binding.playingListRecyclerView.visibility = View.VISIBLE
        binding.circularProgressIndicator.visibility = View.GONE
        binding.circularProgressIndicator.progress = 20
    }

    private fun emitLoadingState() {
        binding.playingListRecyclerView.visibility = View.GONE
        binding.circularProgressIndicator.visibility = View.VISIBLE
    }
}

enum class Factor {
    ARTIST, ALBUM
}
