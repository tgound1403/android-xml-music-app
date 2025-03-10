package com.tmatraining.musicapp.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.media.app.NotificationCompat.MediaStyle
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.presentation.activities.PlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

object PlaybackState {
    val currentSong = MutableLiveData<Song>()
    val isPlaying = MutableLiveData(false)
    val currentList = MutableLiveData<List<Song>>()
    val currentPosition = MutableLiveData<Int>()
}

class MusicService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    companion object {
        private const val CHANNEL_ID = "music_channel"
    }

    private val mediaPlayer = MediaPlayer()
    private val binder = MusicBinder()
    private var isReady = false
    private var positionUpdateJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnErrorListener(this)
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                "PLAY" -> if (isPlaying()) pause() else continuePlay()
                "PAUSE" -> pause()
                "NEXT" -> playNext()
                "PREVIOUS" -> playPrev()
            }
        }
        startForeground(1, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPositionUpdate()
        mediaPlayer.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun continuePlay() {
        if (isReady && !isPlaying()) {
            mediaPlayer.start()
            PlaybackState.isPlaying.value = true
            updateNotification()
            startPositionUpdate()
        }
    }

    fun startPlaying(song: Song) {
        try {
            mediaPlayer.reset()
            isReady = false
            mediaPlayer.setDataSource(this, Uri.parse(song.uri))
            mediaPlayer.prepare()
            isReady = true
            mediaPlayer.start()
            PlaybackState.currentSong.value = song
            PlaybackState.isPlaying.value = true
            PlaybackState.currentPosition.value = 0
            updateNotification()
            startPositionUpdate()
        } catch (e: IOException) {
            PlaybackState.isPlaying.value = false
            PlaybackState.currentPosition.value = 0
        } catch (e: IllegalStateException) {
            PlaybackState.isPlaying.value = false
            PlaybackState.currentPosition.value = 0
        }
    }

    fun playNext() {
        val currentList = PlaybackState.currentList.value ?: return
        val currentIndex = currentList.indexOf(PlaybackState.currentSong.value)
        if (currentIndex != -1 && currentIndex < currentList.size - 1) {
            startPlaying(currentList[currentIndex + 1])
        }
    }

    fun playPrev() {
        val currentList = PlaybackState.currentList.value ?: return
        val currentIndex = currentList.indexOf(PlaybackState.currentSong.value)
        if (currentIndex > 0) {
            startPlaying(currentList[currentIndex - 1])
        }
    }

    fun pause() {
        if (isPlaying()) {
            mediaPlayer.pause()
            PlaybackState.isPlaying.value = false
            updateNotification()
            stopPositionUpdate()
        }
    }

    fun isPlaying(): Boolean = mediaPlayer.isPlaying

    private fun getCurrentPosition(): Int = if (isReady) mediaPlayer.currentPosition else 0

    fun seekTo(position: Int) {
        if (isReady) {
            mediaPlayer.seekTo(position)
            PlaybackState.currentPosition.value = position
            if (!isPlaying()) {
                continuePlay()
            }
        }
    }

    fun getCurrentSong(): Song? = PlaybackState.currentSong.value

    private fun startPositionUpdate() {
        stopPositionUpdate()
        positionUpdateJob = serviceScope.launch {
            while (isActive && isPlaying()) {
                PlaybackState.currentPosition.postValue(getCurrentPosition())
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdate() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        playNext()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        PlaybackState.isPlaying.value = false
        PlaybackState.currentPosition.value = 0
        isReady = false
        updateNotification()
        stopPositionUpdate()
        return true
    }

    private fun createNotification(): Notification {
        val currentSong = PlaybackState.currentSong.value ?: return createDefaultNotification()
        val isPlaying = PlaybackState.isPlaying.value ?: false

        val mainIntent = Intent(this, PlayerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingMainIntent = PendingIntent.getActivity(
            this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(currentSong.title)
            setContentText(currentSong.artist).setColor(resources.getColor(R.color.black))
            setContentIntent(pendingMainIntent).setOngoing(true)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setStyle(MediaStyle().setShowActionsInCompactView(0, 1, 2))
            setLargeIcon(MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(currentSong.albumArtUri)))

            addAction(R.drawable.skip_previous_24px, "Previous", createPendingIntent("PREVIOUS"))

            if (isPlaying) {
                addAction(R.drawable.stop_24px, "Pause", createPendingIntent("PAUSE")).setOngoing(false)
            } else {
                addAction(R.drawable.play_arrow_24px, "Play", createPendingIntent("PLAY"))
            }

            addAction(R.drawable.skip_next_24px, "Next", createPendingIntent("NEXT"))
        }.build()
    }

    private fun createDefaultNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID).setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Music Player")
            .setContentText("No song playing").setOngoing(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, createNotification())
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicService::class.java).apply { this.action = action }
        return PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Music Playback", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for music playback notifications"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}