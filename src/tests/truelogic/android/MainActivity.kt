package com.hackerrank.android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.hackerrank.android.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val autoScrollDuration = 4_000L
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_logo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initUI()
    }

    private val slidingBannerList by lazy {
        listOf(R.drawable.space_journey, R.drawable.the_jungle, R.drawable.hustlers, R.drawable.the_final_race, R.drawable.the_dusk)
    }

    private val headerHandler: Handler = Handler(Looper.getMainLooper())
    private val headerRunnable: Runnable = Runnable {
        binding.imageSlider.setCurrentItem(
            binding.imageSlider.currentItem + 1,
            true
        )
    }

    override fun onResume() {
        super.onResume()
        headerHandler.postDelayed(headerRunnable, autoScrollDuration)
    }

    override fun onPause() {
        super.onPause()
        headerHandler.removeCallbacks(headerRunnable)
    }

    private fun initUI() {
        binding.apply {
            val drawableIds = buildList<Int> {
                add(slidingBannerList.last())
                addAll(slidingBannerList)
                add(slidingBannerList.first())
            }

            

            imageSlider.apply {
                offscreenPageLimit = 1
                adapter = SlidingImageAdapter(drawableIds = drawableIds)
                // setting the current item of the infinite ViewPager to the actual first element
                currentItem = 1
            }
            inflateIndicatorTabs()
            // Write code to pass the correct totalItemCount in the below method
            handleViewPagerCallbacks(totalItemCount = drawableIds.size)
        }
    }

    private fun inflateIndicatorTabs() {
        binding.apply {
            for (i in slidingBannerList.indices) {
                if (i == 0)
                    imageSliderIndicators.addTab(imageSliderIndicators.newTab(), true)
                else
                    imageSliderIndicators.addTab(imageSliderIndicators.newTab())
            }
        }
    }

    private fun handleViewPagerCallbacks(totalItemCount: Int) {
        with(binding) {
            imageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                /**
                 * Called when the scroll state changes. Useful for discovering when the user begins
                 * dragging, when a fake drag is started, when the pager is automatically settling to the
                 * current page, or when it is fully stopped/idle.
                 */
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                       when (val pos = imageSlider.currentItem){
                          0 -> imageSlider.setCurrentItem((totalItemCount - 2), false)
                          totalItemCount - 1 -> imageSlider.setCurrentItem(1, false)
                       }
                    }
                }

                /**
                 * This method will be invoked when a new page becomes selected. Animation is not
                 * necessarily complete.
                 *
                 * @param position Position index of the new selected page.
                 */
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position != 0 && position != totalItemCount - 1) {
                        
                        val realCount = slidingBannerList.size
                        val posReal = when(position){
                            0 -> realCount -1
                            totalItemCount - 1 -> 0
                            else -> position -1
                        }
                        imageSliderIndicators.getTabAt(posReal)?.select()

                        headerHandler.removeCallbacks(headerRunnable)
                        headerHandler.postDelayed(headerRunnable, autoScrollDuration)
                    }
                }
            })
        }
    }
}