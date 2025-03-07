package com.tmatraining.musicapp.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.MiniPlayerBinding
import com.tmatraining.musicapp.presentation.activities.PlayerActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MiniPlayerFragment() : Fragment() {
    companion object {
        private const val TAG = "MiniPlayerFragment"
    }
    private lateinit var _binding: MiniPlayerBinding
    private val binding get() = _binding
    private var song: Song? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = MiniPlayerBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        song = PlaybackState.currentSong.value!!
        binding.apply {
            controlButton.setImageResource(R.drawable.stop_24px)
            miniSongTitle.text = song?.title
            miniSongArtist.text = song?.artist
            songCover.setImageURI(Uri.parse(song?.albumArtUri))
            PlaybackState.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
                controlButton.setOnClickListener {
                    if (!isPlaying) {
                        controlButton.setImageResource(R.drawable.stop_24px)
                        EventBus.getDefault().post(MiniPlayerEvent(MiniPlayerAction.PLAY))
                    } else {
                        controlButton.setImageResource(R.drawable.play_arrow_24px)
                        EventBus.getDefault().post(MiniPlayerEvent(MiniPlayerAction.PAUSE))
                    }
                }
            }
            root.setOnClickListener {
                Intent(requireContext(), PlayerActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMiniPlayerEvent(event: MiniPlayerEvent?) {}
}

data class MiniPlayerEvent(val action: MiniPlayerAction)

enum class MiniPlayerAction {
    PLAY,
    PAUSE
}

