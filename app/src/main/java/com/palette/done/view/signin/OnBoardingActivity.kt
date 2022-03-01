package com.palette.done.view.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.palette.done.R
import com.palette.done.databinding.ActivityOnBoardingBinding
import com.palette.done.view.adapter.ViewPagerAdapter

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBackButtonAndIndicator()
        setViewPager()
    }

    private fun setBackButtonAndIndicator() {
        binding.btnBack.setOnClickListener {
            setBackButton()
        }
    }

    private fun setBackButton() {
        var current = binding.viewPagerOnBoarding.currentItem
        when(current) {
            1 ->{
                binding.viewPagerOnBoarding.currentItem = 0
                binding.ivIndicatorSecond.setImageResource(R.drawable.ic_indicator_left)
                binding.ivIndicatorThird.setImageResource(R.drawable.ic_indicator_left)}
            2 -> {
                binding.viewPagerOnBoarding.currentItem = 1
                binding.ivIndicatorSecond.setImageResource(R.drawable.ic_indicator_now)
                binding.ivIndicatorThird.setImageResource(R.drawable.ic_indicator_left)
            }
        }
    }

    private fun setViewPager() {
        val fragments = arrayListOf<Fragment>(ObNicknameFragment(), ObTypeFragment(), ObAlarmFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewPagerOnBoarding.adapter = adapter
        binding.viewPagerOnBoarding.isUserInputEnabled = false  // swipe action 제거
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        setBackButton()
    }


}