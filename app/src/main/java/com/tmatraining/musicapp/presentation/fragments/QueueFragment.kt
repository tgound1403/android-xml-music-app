package com.tmatraining.musicapp.presentation.fragments

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.FragmentQueueBinding
import com.tmatraining.musicapp.presentation.adapters.TracksAdapter
import dagger.hilt.android.scopes.FragmentScoped
import org.greenrobot.eventbus.EventBus

@FragmentScoped
class QueueFragment : Fragment() {

    private var _binding: FragmentQueueBinding? = null
    private val binding get() = _binding!!
    private lateinit var tracksAdapter: TracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emitLoadingState()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        observePlaybackState()
    }

    override fun onPause() {
        super.onPause()
        PlaybackState.currentList.value = tracksAdapter.getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.playingListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupSwipeGestures()
    }

    private fun observePlaybackState() {
        PlaybackState.currentList.observe(viewLifecycleOwner) { tracks ->
            val currentPlaying = PlaybackState.currentSong.value
            val index = tracks.indexOf(currentPlaying)
            val queueList = tracks.subList(index, tracks.size).toMutableList()

            tracksAdapter = TracksAdapter(queueList) { song ->
                EventBus.getDefault().post(PlayEvent(song))
            }
            binding.playingListRecyclerView.adapter = tracksAdapter

            if (queueList.isNotEmpty()) {
                emitLoadSuccessState()
            } else {
                emitEmptyDataState()
            }
        }

        PlaybackState.currentSong.observe(viewLifecycleOwner) {
            tracksAdapter.notifyDataSetChanged()
        }
    }

    private fun setupSwipeGestures() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val foregroundLayout = itemView.findViewById<CardView>(R.id.foreground_layout)
                val backgroundLayout = itemView.findViewById<LinearLayout>(R.id.background_layout)

                backgroundLayout.visibility = View.VISIBLE

                val maxSwipeDistance = backgroundLayout.width.toFloat()
                val newWidth = (foregroundLayout.width + dX).coerceAtLeast(foregroundLayout.width - maxSwipeDistance).toInt()
                foregroundLayout.layoutParams.width = newWidth
                foregroundLayout.requestLayout()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                val foregroundLayout = viewHolder.itemView.findViewById<CardView>(R.id.foreground_layout)
                val backgroundLayout = viewHolder.itemView.findViewById<LinearLayout>(R.id.background_layout)

                foregroundLayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                foregroundLayout.requestLayout()
                backgroundLayout.visibility = View.GONE

                super.clearView(recyclerView, viewHolder)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.playingListRecyclerView)
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