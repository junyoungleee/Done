package com.palette.done.view

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.palette.done.R
import com.palette.done.data.PreferenceManager
import com.palette.done.databinding.ActivityLandingBinding
import com.palette.done.view.adapter.LandingAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = arrayListOf(
            LandingItem(getString(R.string.landing_title_1), getString(R.string.landing_detail_1),
                ContextCompat.getDrawable(this, R.drawable.img_landing_1)!!
            ),
            LandingItem(getString(R.string.landing_title_2), getString(R.string.landing_detail_2),
                ContextCompat.getDrawable(this, R.drawable.img_landing_2)!!
            ),
            LandingItem(getString(R.string.landing_title_3), getString(R.string.landing_detail_3),
                ContextCompat.getDrawable(this, R.drawable.img_landing_3)!!
            )
        )

        binding.vpLanding.adapter = LandingAdapter(item)
        binding.vpLanding.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setNextButton(position)
            }
        })
        binding.vpIndicator.setViewPager2(binding.vpLanding)
    }

    private fun setNextButton(position: Int) {
        when(position) {
            0, 1 -> {
                with(binding.btnNextStart) {
                    text = getString(R.string.landing_btn_next)
                    setOnClickListener {
                        binding.vpLanding.currentItem = position+1
                    }
                }
            }
            2 -> {
                with(binding.btnNextStart) {
                    text = getString(R.string.landing_start)
                    setOnClickListener {
                        val intent = Intent(context, StartActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

class LandingItem(var title: String, var detail: String, var image: Drawable)