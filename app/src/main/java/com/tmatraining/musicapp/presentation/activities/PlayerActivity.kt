package com.tmatraining.musicapp.presentation.activities

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tmatraining.musicapp.R
import com.tmatraining.musicapp.databinding.ActivityPlayerBinding
import com.tmatraining.musicapp.presentation.fragments.DetailFragment
import com.tmatraining.musicapp.presentation.fragments.EmptyFragment
import com.tmatraining.musicapp.presentation.fragments.PlayerFragment
import com.tmatraining.musicapp.presentation.fragments.QueueFragment
import com.tmatraining.musicapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityPlayerBinding
    private lateinit var activityTabLayout: TabLayout
    private lateinit var activityViewPager: ViewPager2
    private val TAG = this.javaClass.simpleName
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        mainViewModel.fetchSongOnDevice()
        mainViewModel.getArtists()
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        _binding.apply {
            backBtn.setImageResource(R.drawable.chevron_left_24px)
            menuBtn?.setImageResource(R.drawable.menu_24px)
            backBtn.setOnClickListener {
                finish()
            }
            activityViewPager = this.viewPager
            activityTabLayout = this.tabLayout
        }

        setUpViewPager()
        setUpTabLayout()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    private fun setUpViewPager() {
        activityViewPager.apply {
            adapter = object : FragmentStateAdapter(this@PlayerActivity) {
                override fun getItemCount(): Int = 3

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> QueueFragment()
                        1 -> PlayerFragment()
                        2 -> DetailFragment()
                        else -> EmptyFragment()
                    }
                }
            }
            isUserInputEnabled = false
            currentItem = 1
        }
    }

    private fun setUpTabLayout() {
        fun loadDrawable(iconId: Int): Drawable? {
            return Icon.createWithResource(this@PlayerActivity, iconId)
                .loadDrawable(this@PlayerActivity)
        }

        TabLayoutMediator(activityTabLayout, activityViewPager) { tab, position ->
            tab.icon = when (position) {
                0 -> loadDrawable(R.drawable.queue_music_24px)
                1 -> loadDrawable(R.drawable.books_movies_and_music_24px)
                2 -> loadDrawable(R.drawable.artist_24px)
                else -> null
            }
        }.attach()
    }
}