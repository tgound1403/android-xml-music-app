package com.tmatraining.musicapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.FragmentQueueBinding
import com.tmatraining.musicapp.presentation.adapters.TracksAdapter
import dagger.hilt.android.scopes.FragmentScoped
import org.greenrobot.eventbus.EventBus

@FragmentScoped
class QueueFragment : Fragment() {
    private lateinit var _binding: FragmentQueueBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emitLoadingState()
    }

    override fun onResume() {
        super.onResume()
        PlaybackState.currentList.observe(viewLifecycleOwner) {
            val currentPlaying = PlaybackState.currentSong.value
            val index = it.indexOf(currentPlaying)
            _binding.playingListRecyclerView.apply {
                adapter = TracksAdapter(it.subList(index, it.size)) { song ->
                    EventBus.getDefault().post(PlayEvent(song))
                }
                layoutManager = LinearLayoutManager(this@QueueFragment.requireContext())
            }
            if (it.isNotEmpty()) {
                emitLoadSuccessState()
            } else {
                emitEmptyDataState()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(this.javaClass.simpleName, "onPause")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentQueueBinding.inflate(inflater, container, false)
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

    private fun emitLoadingState() {
        _binding.playingListRecyclerView.visibility = View.GONE
        _binding.circularProgressIndicator.visibility = View.VISIBLE
        _binding.textView.visibility = View.GONE

    }
}