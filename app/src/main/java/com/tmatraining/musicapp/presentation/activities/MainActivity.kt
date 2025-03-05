package com.tmatraining.musicapp.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.core.db.entity.Song
import com.tmatraining.musicapp.core.services.PlaybackState
import com.tmatraining.musicapp.databinding.ActivityMainBinding
import com.tmatraining.musicapp.presentation.fragments.AlbumsFragment
import com.tmatraining.musicapp.presentation.fragments.ArtistsFragment
import com.tmatraining.musicapp.presentation.fragments.EmptyFragment
import com.tmatraining.musicapp.presentation.fragments.MiniPlayerFragment
import com.tmatraining.musicapp.presentation.fragments.PlaylistsFragment
import com.tmatraining.musicapp.presentation.fragments.TracksFragment
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var activityViewPager: ViewPager2
    private lateinit var activityTabLayout: TabLayout

    private val mainViewModel: MainViewModel by viewModels()
    private var data = emptyList<Song>()

    private val dataObserver = { e: List<Song> -> data = e }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            return false
        }
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkPermission()) {
            lifecycleScope.launch(Dispatchers.IO) {
                mainViewModel.fetchSongOnDevice()
                mainViewModel.getArtists()
                mainViewModel.getAlbums()
                mainViewModel.getPlaylists()
            }
        } else {
            checkPermission()
        }

        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this)).also {
            activityViewPager = it.viewPager
            activityTabLayout = it.tabLayout
            setContentView(it.root)
        }

        mainViewModel.tracks.observe(this, dataObserver)

        setUpViewPager()
        setUpTabLayout()
    }

    override fun onResume() {
        super.onResume()
        if (PlaybackState.currentSong.value != null) {
            with(MiniPlayerFragment()) {
                if (!supportFragmentManager.fragments.contains(this)) {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.overlay_container, this)
                        .commit()
                }
            }
        }
    }

    private fun setUpViewPager() {
        activityViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> TracksFragment()
                    1 -> PlaylistsFragment()
                    2 -> ArtistsFragment()
                    3 -> AlbumsFragment()
                    else -> EmptyFragment()
                }
            }
        }
        activityViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                _binding.tabTitle?.text  = when (position) {
                    0 -> HomeTab.TRACKS.title
                    1 -> HomeTab.PLAYLISTS.title
                    2 -> HomeTab.ARTISTS.title
                    3 -> HomeTab.ALBUMS.title
                    else -> HomeTab.NONE.title
                }
            }
        })
    }

    private fun setUpTabLayout() {
        fun loadDrawable(iconId: Int): Drawable? {
            return Icon.createWithResource(this@MainActivity, iconId)
                .loadDrawable(this@MainActivity)
        }

        TabLayoutMediator(activityTabLayout, activityViewPager) { tab, position ->
            tab.icon = when (position) {
                0 -> loadDrawable(R.drawable.play_arrow_24px)
                1 -> loadDrawable(R.drawable.books_movies_and_music_24px)
                2 -> loadDrawable(R.drawable.artist_24px)
                3 -> loadDrawable(R.drawable.queue_music_24px)
                else -> null
            }
            tab.text = when (position) {
                0 -> HomeTab.TRACKS.title
                1 -> HomeTab.PLAYLISTS.title
                2 -> HomeTab.ARTISTS.title
                3 -> HomeTab.ALBUMS.title
                else -> HomeTab.NONE.title
            }
        }.attach()
    }


}

enum class HomeTab(val title: String) {
    TRACKS("Tracks"),
    PLAYLISTS("Playlists"),
    ARTISTS("Artists"),
    ALBUMS("Albums"),
    NONE("");
}
