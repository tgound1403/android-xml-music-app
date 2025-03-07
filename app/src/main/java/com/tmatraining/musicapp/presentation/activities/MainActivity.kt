package com.tmatraining.musicapp.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
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
    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val TAG = "MainActivity"
    }

    private lateinit var _binding: ActivityMainBinding
    private var activityViewPager: ViewPager2? = null
    private var activityTabLayout: TabLayout? = null

    private val mainViewModel: MainViewModel by viewModels()
    private var data = emptyList<Song>()
    private var currentPosition: Int? = null
    private lateinit var currentFragment: Fragment


    private val dataObserver = { e: List<Song> -> data = e }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate $data")

        requestStoragePermission()
        setupOrientationLayout(resources.configuration)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchMediaData()
        }
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        } else {
            fetchMediaData()
        }
    }

    private fun fetchMediaData() {
        lifecycleScope.launch(Dispatchers.IO) {
            with(mainViewModel) {
                fetchSongOnDevice()
                getArtists()
                getAlbums()
                getPlaylists()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupOrientationLayout(newConfig)
    }

    private fun setupOrientationLayout(config: Configuration) {
        when (config.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> buildLayoutOnPortraitMode()
            Configuration.ORIENTATION_LANDSCAPE -> buildLayoutOnLandscapeMode()
        }
    }

    private fun buildLayoutOnPortraitMode() {
        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this)).also {
            activityViewPager = it.viewPager!!
            activityTabLayout = it.tabLayout!!
            setContentView(it.root)
        }

        mainViewModel.tracks.observe(this, dataObserver)

        setUpViewPager()
        setUpTabLayout()
    }

    private fun buildLayoutOnLandscapeMode() {
        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this)).also {
            setContentView(it.root)
            it.tracksBtn?.setOnClickListener {
                changeToFragment(TracksFragment())
            }
            it.playlistsBtn?.setOnClickListener {
                changeToFragment(PlaylistsFragment())
            }
            it.artistsBtn?.setOnClickListener {
                changeToFragment(ArtistsFragment())
            }
            it.albumsBtn?.setOnClickListener {
                changeToFragment(AlbumsFragment())
            }

            currentFragment = when (currentPosition) {
                0 -> TracksFragment()
                1 -> PlaylistsFragment()
                2 -> ArtistsFragment()
                3 -> AlbumsFragment()
                else -> EmptyFragment()
            }
            it.tabTitle.text = when (currentPosition) {
                0 -> getString(R.string.tracks)
                1 -> getString(R.string.playlists)
                2 -> getString(R.string.artist)
                3 -> getString(R.string.albums)
                else -> ""
            }

            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, currentFragment)
                .addToBackStack(null).commit()
        }
    }

    private fun changeToFragment(fragment: Fragment) {
        _binding.tabTitle.text = when (fragment) {
            is TracksFragment -> getString(R.string.tracks)
            is PlaylistsFragment -> getString(R.string.playlists)
            is ArtistsFragment -> getString(R.string.artist)
            is AlbumsFragment -> getString(R.string.albums)
            else -> ""
        }
        supportFragmentManager.apply {
            popBackStack()
            beginTransaction().remove(currentFragment).add(R.id.fragmentContainer, fragment)
                .addToBackStack(null).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        showMiniPlayer()
    }

    private fun showMiniPlayer() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (PlaybackState.currentSong.value != null) {
                with(MiniPlayerFragment()) {
                    if (!supportFragmentManager.fragments.contains(this)) {
                        supportFragmentManager.beginTransaction().add(R.id.overlay_container, this)
                            .commit()
                    }
                }
            }
        }
    }

    private fun setUpViewPager() {
        activityViewPager?.adapter = object : FragmentStateAdapter(this) {
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
        activityViewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                _binding.tabTitle.text = when (position) {
                    0 -> getString(R.string.tracks)
                    1 -> getString(R.string.playlists)
                    2 -> getString(R.string.artist)
                    3 -> getString(R.string.albums)
                    else -> ""
                }
            }
        })
    }

    private fun setUpTabLayout() {
        fun loadDrawable(iconId: Int): Drawable? {
            return Icon.createWithResource(this@MainActivity, iconId)
                .loadDrawable(this@MainActivity)
        }

        TabLayoutMediator(activityTabLayout!!, activityViewPager!!) { tab, position ->
            tab.icon = when (position) {
                0 -> loadDrawable(R.drawable.play_arrow_24px)
                1 -> loadDrawable(R.drawable.books_movies_and_music_24px)
                2 -> loadDrawable(R.drawable.artist_24px)
                3 -> loadDrawable(R.drawable.queue_music_24px)
                else -> null
            }
            tab.text = when (position) {
                0 -> getString(R.string.tracks)
                1 -> getString(R.string.playlists)
                2 -> getString(R.string.artist)
                3 -> getString(R.string.albums)
                else -> ""
            }
        }.attach()
    }

}

