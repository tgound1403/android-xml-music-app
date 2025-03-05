package com.tmatraining.musicapp.presentation.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.SeekBar
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.services.MusicService
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.FragmentPlayerBinding
import com.tmatraining.musicapp.presentation.viewmodels.PlayerViewModel
import dagger.hilt.android.scopes.FragmentScoped
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.time.Duration.Companion.milliseconds

@FragmentScoped
class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModels()
    private var musicService: MusicService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            viewModel.setMusicService(musicService!!)
            viewModel.updatePosition()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val serviceIntent = Intent(requireActivity(), MusicService::class.java)
        startForegroundService(requireActivity(), serviceIntent)
        requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            prevBtn.setImageResource(R.drawable.skip_previous_24px)
            nextBtn.setImageResource(R.drawable.skip_next_24px)

            PlaybackState.currentSong.observe(viewLifecycleOwner) { song ->
                if (PlaybackState.isPlaying.value == true) animateCoverTransition(song)
                prevBtn.setOnClickListener {
                    it.startButtonAnimation()
                    musicService?.playPrev()
                }
                nextBtn.setOnClickListener {
                    it.startButtonAnimation()
                    musicService?.playNext()
                }
            }

            PlaybackState.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
                playBtn.setImageResource(if (isPlaying) R.drawable.stop_24px else R.drawable.play_arrow_24px)
                playBtn.setOnClickListener {
                    viewModel.togglePlayPause()
                }
            }

            viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
                currentTime.text = position.toLong().milliseconds.toString()
                seekBar.progress = position
                if (position >= seekBar.max) {
                    musicService?.playNext()
                    updateUI(PlaybackState.currentSong.value!!)
                }
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    if (fromUser) viewModel.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        musicService?.continuePlay()
    }

    override fun onResume() {
        super.onResume()
        if (PlaybackState.isPlaying.value != true) viewModel.playSong(PlaybackState.currentSong.value!!)
        PlaybackState.currentSong.value?.let { updateUI(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(song: Song) {
        binding.apply {
            artist.text = song.artist
            songName.text = song.title
            cover.setImageURI(Uri.parse(song.albumArtUri))
            seekBar.max = song.duration.toInt()
            totalTime.text = song.duration.milliseconds.toString()
        }
    }

    private fun animateCoverTransition(newSong: Song) {
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = 300
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.cover.setImageURI(Uri.parse(newSong.albumArtUri))
                    updateUI(newSong)

                    val fadeIn = AlphaAnimation(0f, 1f).apply {
                        duration = 300
                        fillAfter = true
                    }
                    binding.cover.startAnimation(fadeIn)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        binding.cover.startAnimation(fadeOut)
    }

    private fun View.startButtonAnimation() {
        val scaleUp = ScaleAnimation(
            1f, 1.2f,
            1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 100
            fillAfter = false
        }

        val scaleDown = ScaleAnimation(
            1.2f, 1f,
            1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 100 //
            fillAfter = false
        }

        scaleUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                startAnimation(scaleDown)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        startAnimation(scaleUp)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMiniPlayerEvent(event: MiniPlayerEvent?) {
        if (event?.action == "play") {
            musicService?.continuePlay()
        } else if (event?.action == "pause") {
            musicService?.pause()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayEvent(event: PlayEvent) {
        musicService?.startPlaying(event.data)
    }
}

data class PlayEvent(
    val data: Song,
)